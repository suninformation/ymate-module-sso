/*
 * Copyright 2007-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.sso.controller;

import net.ymate.framework.commons.ParamUtils;
import net.ymate.framework.core.Optional;
import net.ymate.framework.core.util.WebUtils;
import net.ymate.framework.webmvc.ErrorCode;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.sso.ISSOToken;
import net.ymate.module.sso.ISSOTokenAttributeAdapter;
import net.ymate.module.sso.ISSOTokenStorageAdapter;
import net.ymate.module.sso.SSO;
import net.ymate.platform.core.util.ExpressionUtils;
import net.ymate.platform.webmvc.annotation.Controller;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.HttpStatusView;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/5/4 上午1:29
 * @version 1.0
 */
@Controller
@RequestMapping("/sso")
public class SSOTokenController {

    /**
     * <p>
     * 当存在跨域获取SSO令牌的情况时，需要调整SSO客户端的参数配置：webmvc.redirect_login_url=sso/authorize?redirect_url=${redirect_url}，服务端则仍保持原参数配置不变即可。
     * </p>
     *
     * @param redirectUrl 重定向URL地址
     * @return 尝试获取当前登录用户的SSO令牌(主要适用于跨域单点登录)
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/authorize")
    public IView __toAuthorize(@RequestParam(Optional.REDIRECT_URL) String redirectUrl) throws Exception {
        if (StringUtils.isBlank(redirectUrl) || StringUtils.contains(redirectUrl, "/sso/authorize")) {
            return HttpStatusView.METHOD_NOT_ALLOWED;
        }
        //
        if (SSO.get().getModuleCfg().isClientMode()) {
            Map<String, String> _params = new HashMap<String, String>();
            _params.put(Optional.REDIRECT_URL, redirectUrl);
            // 当客户端访问该控制器方法时，将请求重定向至服务端
            return View.redirectView(ParamUtils.appendQueryParamValue(SSO.get().getModuleCfg().getServiceBaseUrl().concat("sso/authorize"), _params, true, WebContext.getContext().getOwner().getModuleCfg().getDefaultCharsetEncoding()));
        }
        //
        ISSOToken _token = SSO.get().currentToken();
        if (_token != null) {
            Map<String, String> _params = new HashMap<String, String>();
            _params.put(SSO.get().getModuleCfg().getTokenParamName(), SSO.get().getModuleCfg().getTokenAdapter().encryptToken(_token));
            // 当前服务端用户已登录，则重定向至redirectUrl地址将携带token参数
            return View.redirectView(ParamUtils.appendQueryParamValue(redirectUrl, _params, true, WebContext.getContext().getOwner().getModuleCfg().getDefaultCharsetEncoding()));
        }
        // 当前服务端用户尚未登录，则重定向登录视图
        String _redirectUrl = WebUtils.buildRedirectURL(null, StringUtils.defaultIfBlank(WebContext.getContext().getOwner().getOwner().getConfig().getParam(Optional.REDIRECT_LOGIN_URL), "login?redirect_url=${redirect_url}"), true);
        _redirectUrl = ExpressionUtils.bind(_redirectUrl).set(Optional.REDIRECT_URL, WebUtils.encodeURL(redirectUrl)).getResult();
        return View.redirectView(_redirectUrl);
    }

    /**
     * @param tokenId    令牌唯一标识
     * @param uid        用户唯一标识
     * @param remoteAddr 用户IP地址
     * @param sign       参数签名
     * @return 验证客户端令牌有效性及状态
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/authorize", method = Type.HttpMethod.POST)
    public IView __doAuthorize(@RequestParam("token_id") String tokenId,
                               @RequestParam String uid,
                               @RequestParam("remote_addr") String remoteAddr,
                               @RequestParam String sign) throws Exception {

        if (SSO.get().getModuleCfg().isClientMode()) {
            return HttpStatusView.METHOD_NOT_ALLOWED;
        }
        //
        if (StringUtils.isNotBlank(tokenId) && StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(remoteAddr) && StringUtils.isNotBlank(sign)) {
            Map<String, String> _params = new HashMap<String, String>();
            _params.put("token_id", tokenId);
            _params.put("uid", uid);
            _params.put("remote_addr", remoteAddr);
            //
            String _sign = ParamUtils.createSignature(_params, false, SSO.get().getModuleCfg().getServiceAuthKey());
            if (StringUtils.equals(sign, _sign)) {
                ISSOTokenStorageAdapter _storageAdapter = SSO.get().getModuleCfg().getTokenStorageAdapter();
                // 尝试从存储中加载原始令牌数据并进行有效性验证
                ISSOToken _token = _storageAdapter.load(uid, tokenId);
                if (_token != null) {
                    boolean _ipCheck = (SSO.get().getModuleCfg().isIpCheckEnabled() && !StringUtils.equals(remoteAddr, _token.getRemoteAddr()));
                    if (_token.timeout() || !_token.verified() || _ipCheck) {
                        _storageAdapter.remove(_token.getUid(), _token.getId());
                        return WebResult.CODE(ErrorCode.USER_SESSION_INVALID_OR_TIMEOUT).toJSON();
                    } else {
                        WebResult _result = WebResult.SUCCESS();
                        // 尝试加载令牌自定义属性
                        ISSOTokenAttributeAdapter _attributeAdapter = SSO.get().getModuleCfg().getTokenAttributeAdapter();
                        if (_attributeAdapter != null) {
                            _attributeAdapter.loadAttributes(_token);
                            if (!_token.getAttributes().isEmpty()) {
                                _result.data(_token.getAttributes());
                            }
                        }
                        return _result.toJSON();
                    }
                }
            }
        }
        return WebResult.CODE(ErrorCode.INVALID_PARAMS_VALIDATION).toJSON();
    }
}
