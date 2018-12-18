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
package net.ymate.module.sso.impl;

import net.ymate.module.sso.ISSO;
import net.ymate.module.sso.ISSOToken;
import net.ymate.module.sso.ISSOTokenAdapter;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.core.util.UUIDUtils;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.CookieHelper;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午3:52
 * @version 1.0
 */
public class DefaultSSOTokenAdapter implements ISSOTokenAdapter {

    private static final Log _LOG = LogFactory.getLog(DefaultSSOTokenAdapter.class);

    private ISSO __owner;

    @Override
    public void init(ISSO owner) throws Exception {
        __owner = owner;
    }

    @Override
    public void destroy() throws Exception {
        __owner = null;
    }

    @Override
    public String generateTokenKey() {
        return UUIDUtils.UUID();
    }

    @Override
    public ISSOToken getToken() {
        ISSOToken _token = null;
        try {
            // 优先从Cookie中获取Token数据
            String _tokenStr = CookieHelper.bind(WebContext.getContext().getOwner())
                    .getCookie(__owner.getModuleCfg().getTokenCookieName())
                    .toStringValue();
            _token = decryptToken(_tokenStr);
            if (_token == null) {
                // 尝试从请求头中获取Token数据
                _token = decryptToken(WebContext.getRequest().getHeader(__owner.getModuleCfg().getTokenHeaderName()));
                //
                if (_token == null && __owner.getModuleCfg().isClientMode()) {
                    // 若当前为客户端模式，则尝试从请求参数中获取Token数据
                    _token = decryptToken(WebContext.getRequest().getParameter(__owner.getModuleCfg().getTokenParamName()));
                }
            }
        } catch (Exception e) {
            _LOG.warn("An exception occurred while getting token for current user", RuntimeUtils.unwrapThrow(e));
        }
        return _token;
    }

    @Override
    public void setToken(ISSOToken token) throws Exception {
        CookieHelper _cookieHelper = CookieHelper.bind(WebContext.getContext().getOwner());
        String _tokenStr = encryptToken(token);
        String _tokenCookieName = __owner.getModuleCfg().getTokenCookieName();
        int _maxAge = __owner.getModuleCfg().getTokenMaxage();
        if (_maxAge > 0) {
            _cookieHelper.setCookie(_tokenCookieName, _tokenStr, _maxAge);
        } else {
            _cookieHelper.setCookie(_tokenCookieName, _tokenStr);
        }
    }

    @Override
    public void cleanToken() {
        CookieHelper.bind(WebContext.getContext().getOwner()).removeCookie(__owner.getModuleCfg().getTokenCookieName());
    }

    @Override
    public String encryptToken(ISSOToken token) throws Exception {
        return WebUtils.encryptStr(token.getUid() + "|" + token.getCreateTime(), WebContext.getRequest().getHeader("User-Agent"));
    }

    @Override
    public ISSOToken decryptToken(String tokenSeriStr) throws Exception {
        if (StringUtils.isNotBlank(tokenSeriStr)) {
            String _userAgent = WebContext.getRequest().getHeader("User-Agent");
            String[] _tokenArr = StringUtils.split(WebUtils.decryptStr(tokenSeriStr, _userAgent), "|");
            if (_tokenArr != null && _tokenArr.length == 2) {
                return new DefaultSSOToken(_tokenArr[0], _userAgent, null, BlurObject.bind(_tokenArr[1]).toLongValue()).build();
            }
        }
        return null;
    }
}
