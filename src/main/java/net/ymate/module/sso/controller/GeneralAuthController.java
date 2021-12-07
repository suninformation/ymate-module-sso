/*
 * Copyright 2007-2020 the original author or authors.
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

import net.ymate.module.sso.ISingleSignOnConfig;
import net.ymate.module.sso.IToken;
import net.ymate.module.sso.SingleSignOn;
import net.ymate.module.sso.interceptor.UserSessionStatus;
import net.ymate.platform.commons.util.ExpressionUtils;
import net.ymate.platform.commons.util.ParamUtils;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.WebErrorCode;
import net.ymate.platform.webmvc.util.WebUtils;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.HttpStatusView;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/05/04 01:29
 */
public class GeneralAuthController {

    @Inject
    private SingleSignOn owner;

    /**
     * 当存在跨域获取SSO令牌的情况时，需要调整SSO客户端的参数配置：module.sso.token_invalid_redirect_url=authorize?redirect_url=${redirect_url}，服务端则仍保持原参数配置不变即可。
     * <p>
     * 注意: 需要保证Cookie作用域名包含子域
     *
     * @param redirectUrl 重定向URL地址
     * @return 尝试获取当前登录用户的SSO令牌(主要适用于跨域单点登录)
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(ISingleSignOnConfig.DEFAULT_CONTROLLER_MAPPING)
    @UserSessionStatus
    public IView authorize(@RequestParam(Type.Const.REDIRECT_URL) String redirectUrl) throws Exception {
        if (StringUtils.isBlank(redirectUrl) || StringUtils.contains(redirectUrl, owner.getConfig().getServicePrefix() + ISingleSignOnConfig.DEFAULT_CONTROLLER_MAPPING)) {
            return HttpStatusView.METHOD_NOT_ALLOWED;
        }
        if (owner.getConfig().isClientMode()) {
            // 当客户端访问该控制器方法时，将请求重定向至服务端
            Map<String, String> params = Collections.singletonMap(Type.Const.REDIRECT_URL, redirectUrl);
            return View.redirectView(ParamUtils.appendQueryParamValue(owner.getConfig().getServiceBaseUrl() + owner.getConfig().getServicePrefix() + ISingleSignOnConfig.DEFAULT_CONTROLLER_MAPPING, params, true, WebContext.getContext().getOwner().getConfig().getDefaultCharsetEncoding()));
        }
        IToken token = owner.getCurrentToken();
        if (token != null) {
            // 当前服务端用户已登录，则重定向至redirectUrl地址
            return View.redirectView(redirectUrl);
        }
        // 当前服务端用户尚未登录，则重定向登录视图，若未设置令牌无效重定向URL地址则直接显示错误提示视图
        String tokenInvalidRedirectUrl = owner.getConfig().getTokenInvalidRedirectUrl();
        if (StringUtils.isBlank(tokenInvalidRedirectUrl)) {
            return View.redirectView(ExpressionUtils.bind(WebUtils.buildRedirectUrl(null, WebContext.getRequest(), tokenInvalidRedirectUrl, true))
                    .set(Type.Const.REDIRECT_URL, WebUtils.encodeUrl(redirectUrl)).getResult());
        }
        return WebUtils.buildErrorView(WebUtils.getOwner(), WebErrorCode.userSessionInvalidOrTimeout());
    }
}
