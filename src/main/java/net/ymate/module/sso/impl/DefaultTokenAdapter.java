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
package net.ymate.module.sso.impl;

import net.ymate.module.sso.*;
import net.ymate.platform.commons.http.HttpClientHelper;
import net.ymate.platform.commons.http.IHttpResponse;
import net.ymate.platform.commons.json.IJsonObjectWrapper;
import net.ymate.platform.commons.json.JsonWrapper;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.ParamUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.commons.util.UUIDUtils;
import net.ymate.platform.webmvc.IWebResult;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.CookieHelper;
import net.ymate.platform.webmvc.util.WebResult;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/01/01 03:52
 */
public class DefaultTokenAdapter implements ITokenAdapter {

    private static final Log LOG = LogFactory.getLog(DefaultTokenAdapter.class);

    private static final int TOKEN_PART_LENGTH = 4;

    private ISingleSignOn owner;

    private boolean initialized;

    @Override
    public void initialize(ISingleSignOn owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            this.initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            this.owner = null;
            this.initialized = false;
        }
    }

    @Override
    public String generateTokenKey() {
        return UUIDUtils.UUID();
    }

    @Override
    public boolean validateToken(IToken token) throws Exception {
        try {
            if (owner.getConfig().isClientMode()) {
                Map<String, String> params = new HashMap<>(5);
                params.put(IToken.PARAM_TOKEN_ID, token.getId());
                params.put(IToken.PARAM_UID, token.getUid());
                params.put(IToken.PARAM_REMOTE_ADDR, token.getRemoteAddr());
                params.put(IToken.PARAM_USER_AGENT, token.getUserAgent());
                if (StringUtils.isNotBlank(owner.getConfig().getServiceAuthKey())) {
                    params.put(IToken.PARAM_NONCE, UUIDUtils.randomStr(16, false));
                    params.put(IToken.PARAM_SIGN, ParamUtils.createSignature(params, false, true, owner.getConfig().getServiceAuthKey()));
                }
                String serviceUrl = String.format("%s%s%s", owner.getConfig().getServiceBaseUrl(), owner.getConfig().getServicePrefix(), ISingleSignOnConfig.DEFAULT_CONTROLLER_MAPPING);
                IHttpResponse httpResponse = HttpClientHelper.create().post(serviceUrl, params);
                if (httpResponse != null) {
                    if (httpResponse.getStatusCode() == HttpServletResponse.SC_OK) {
                        IWebResult<?> result = WebResult.builder().fromJson(httpResponse.getContent()).build();
                        if (result.isSuccess()) {
                            // 令牌验证通过，则进行本地Cookie存储
                            owner.getConfig().getTokenAdapter().setToken(token);
                            // 尝试从响应报文中提取并追加token属性数据
                            IJsonObjectWrapper dataObj = JsonWrapper.toJson(result.data()).getAsJsonObject();
                            if (dataObj != null && !dataObj.isEmpty()) {
                                dataObj.toMap().forEach((key, value) -> token.addAttribute(key, BlurObject.bind(value).toStringValue()));
                            }
                            return true;
                        } else if (LOG.isDebugEnabled()) {
                            LOG.debug(httpResponse.toString());
                        }
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug(httpResponse.toString());
                    }
                }
            } else {
                ITokenStorageAdapter tokenStorageAdapter = owner.getConfig().getTokenStorageAdapter();
                // 尝试从存储中加载原始令牌数据并进行有效性验证
                IToken originalToken = tokenStorageAdapter.load(token.getId());
                if (originalToken != null && StringUtils.equals(token.getUid(), originalToken.getUid()) && StringUtils.equals(token.getUserAgent(), originalToken.getUserAgent())) {
                    boolean ipCheckFailed = owner.getConfig().isIpCheckEnabled() && !StringUtils.equals(token.getRemoteAddr(), originalToken.getRemoteAddr());
                    if (owner.isTimeout(originalToken) || ipCheckFailed) {
                        tokenStorageAdapter.remove(originalToken);
                    } else {
                        // 尝试加载令牌自定义属性
                        ITokenAttributeAdapter tokenAttributeAdapter = owner.getConfig().getTokenAttributeAdapter();
                        if (tokenAttributeAdapter != null) {
                            tokenAttributeAdapter.loadAttributes(token);
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("An exception occurred while validate token '%s' for user '%s'", token.getId(), token.getUid()), RuntimeUtils.unwrapThrow(e));
            }
        }
        return false;
    }

    @Override
    public IToken getToken() {
        IToken token = null;
        try {
            HttpServletRequest httpServletRequest = WebContext.getRequest();
            // 优先从请求参数中获取Token数据（一般用于API接口而非浏览器）
            token = decryptToken(httpServletRequest.getParameter(owner.getConfig().getTokenParamName()));
            if (token == null) {
                // 尝试从请求头中获取Token数据
                token = decryptToken(httpServletRequest.getHeader(owner.getConfig().getTokenHeaderName()));
                if (token == null) {
                    // 兼容请求头：Authorization: Bearer <token>
                    String tokenStr = StringUtils.trimToNull(httpServletRequest.getHeader(Type.HttpHead.AUTHORIZATION));
                    if (StringUtils.startsWithIgnoreCase(tokenStr, "Bearer")) {
                        tokenStr = StringUtils.trimToNull(StringUtils.substring(tokenStr, "Bearer".length()));
                    }
                    token = decryptToken(tokenStr);
                    if (token == null) {
                        // 尝试从Cookie中获取Token数据
                        tokenStr = CookieHelper.bind(WebContext.getContext().getOwner())
                                .getCookie(owner.getConfig().getTokenCookieName())
                                .toStringValue();
                        token = decryptToken(tokenStr);
                    }
                }
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("An exception occurred while getting token for current user", RuntimeUtils.unwrapThrow(e));
            }
        }
        return token;
    }

    @Override
    public String setToken(IToken token) throws Exception {
        CookieHelper cookieHelper = CookieHelper.bind(WebContext.getContext().getOwner());
        String tokenStr = encryptToken(token);
        String tokenCookieName = owner.getConfig().getTokenCookieName();
        int maxAge = owner.getConfig().getTokenMaxAge();
        if (maxAge > 0) {
            cookieHelper.setCookie(tokenCookieName, tokenStr, maxAge);
        } else {
            cookieHelper.setCookie(tokenCookieName, tokenStr);
        }
        return tokenStr;
    }

    @Override
    public void cleanToken() {
        CookieHelper.bind(WebContext.getContext().getOwner()).removeCookie(owner.getConfig().getTokenCookieName());
    }

    @Override
    public String encryptToken(IToken token) throws Exception {
        String encryptKey = token.getUserAgent();
        if (StringUtils.isBlank(encryptKey)) {
            encryptKey = WebContext.getRequest().getHeader(Type.HttpHead.USER_AGENT);
        }
        encryptKey += StringUtils.trimToEmpty(owner.getConfig().getServiceAuthKey());
        return WebUtils.encryptStr(String.format("%s|%s|%s|%d", token.getId(), token.getUid(), token.getRemoteAddr(), token.getCreateTime()), encryptKey);
    }

    @Override
    public IToken decryptToken(String tokenStr) throws Exception {
        if (StringUtils.isNotBlank(tokenStr)) {
            try {
                String userAgent = WebContext.getRequest().getHeader(Type.HttpHead.USER_AGENT);
                String encryptKey = userAgent + StringUtils.trimToEmpty(owner.getConfig().getServiceAuthKey());
                String[] tokenArr = StringUtils.split(WebUtils.decryptStr(tokenStr, encryptKey), "|");
                if (tokenArr != null && tokenArr.length == TOKEN_PART_LENGTH) {
                    DefaultToken token = new DefaultToken(tokenArr[1], tokenArr[2], userAgent, BlurObject.bind(tokenArr[3]).toLongValue());
                    token.setId(tokenArr[0]);
                    return token;
                }
            } catch (Exception e) {
                if (!(e instanceof BadPaddingException)) {
                    throw e;
                }
            }
        }
        return null;
    }
}
