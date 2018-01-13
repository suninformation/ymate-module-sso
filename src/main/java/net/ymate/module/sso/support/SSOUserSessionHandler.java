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
package net.ymate.module.sso.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.framework.commons.ParamUtils;
import net.ymate.framework.webmvc.ErrorCode;
import net.ymate.framework.webmvc.IUserSessionHandler;
import net.ymate.framework.webmvc.support.UserSessionBean;
import net.ymate.module.sso.*;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.webmvc.context.WebContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午4:58
 * @version 1.0
 */
public class SSOUserSessionHandler implements IUserSessionHandler {

    private static final Log _LOG = LogFactory.getLog(SSOUserSessionHandler.class);

    @Override
    public UserSessionBean handle(InterceptContext context) throws Exception {
        ISSOTokenAdapter _tokenAdapter = SSO.get().getModuleCfg().getTokenAdapter();
        // 执行至此，表示UserSessionBean不存在，尝试从Cookies中获取令牌信息
        ISSOToken _token = _tokenAdapter.getToken();
        if (_token == null || !_token.verified()) {
            // 若令牌为空则执行一次令牌清理(不论效果如何)
            _tokenAdapter.cleanToken();
        } else if (_token.validationRequired()) {
            if (__doValidateToken(_token)) {
                return _token.updateLastValidateTime().bindUserSessionBean();
            }
            _tokenAdapter.cleanToken();
        }
        return null;
    }

    private boolean __doValidateToken(ISSOToken token) {
        try {
            if (SSO.get().getModuleCfg().isClientMode()) {
                Map<String, String> _params = new HashMap<String, String>();
                _params.put("token_id", token.getId());
                _params.put("uid", token.getUid());
                _params.put("remote_addr", token.getRemoteAddr());
                _params.put("sign", ParamUtils.createSignature(_params, false, SSO.get().getModuleCfg().getServiceAuthKey()));
                IHttpResponse _result = HttpClientHelper.create().post(SSO.get().getModuleCfg().getServiceBaseUrl().concat("sso/authorize"), _params, new Header[]{new BasicHeader("User-Agent", WebContext.getRequest().getHeader("User-Agent"))});
                if (_result != null && _result.getStatusCode() == 200) {
                    JSONObject _resultObj = JSON.parseObject(_result.getContent());
                    if (_resultObj.getIntValue("ret") == ErrorCode.SUCCESSED) {
                        // 令牌验证通过，则进行本地Cookie存储
                        SSO.get().getModuleCfg().getTokenAdapter().setToken(token);
                        // 尝试从响应报文中提取并追加token属性数据
                        JSONObject _dataObj = _resultObj.getJSONObject("data");
                        if (_dataObj != null && !_dataObj.isEmpty()) {
                            for (Map.Entry<String, Object> _attr : _dataObj.entrySet()) {
                                token.getAttributes().put(_attr.getKey(), BlurObject.bind(_attr.getValue()).toStringValue());
                            }
                        }
                        //
                        return true;
                    }
                }
            } else {
                ISSOTokenStorageAdapter _storageAdapter = SSO.get().getModuleCfg().getTokenStorageAdapter();
                // 尝试从存储中加载原始令牌数据并进行有效性验证
                ISSOToken _originalToken = _storageAdapter.load(token.getUid(), token.getId());
                if (_originalToken != null) {
                    boolean _ipCheck = (SSO.get().getModuleCfg().isIpCheckEnabled() && !StringUtils.equals(token.getRemoteAddr(), _originalToken.getRemoteAddr()));
                    if (_originalToken.timeout() || !_originalToken.verified() || _ipCheck) {
                        _storageAdapter.remove(_originalToken.getUid(), _originalToken.getId());
                    } else {
                        // 尝试加载令牌自定义属性
                        ISSOTokenAttributeAdapter _attributeAdapter = SSO.get().getModuleCfg().getTokenAttributeAdapter();
                        if (_attributeAdapter != null) {
                            _attributeAdapter.loadAttributes(token);
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            _LOG.warn("An exception occurred while validate token '" + token.getId() + "' for user '" + token.getUid() + "'", RuntimeUtils.unwrapThrow(e));
        }
        return false;
    }

    @Override
    public boolean verification(UserSessionBean sessionBean) {
        ISSOToken _token = sessionBean.getAttribute(ISSOToken.class.getName());
        if (_token != null) {
            if (_token.timeout() || !_token.verified() || !__doValidateToken(_token)) {
                _token = null;
            }
        }
        if (_token == null) {
            SSO.get().getModuleCfg().getTokenAdapter().cleanToken();
            return false;
        }
        return true;
    }
}
