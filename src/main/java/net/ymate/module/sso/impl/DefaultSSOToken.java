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

import net.ymate.framework.webmvc.support.UserSessionBean;
import net.ymate.module.sso.ISSOToken;
import net.ymate.module.sso.SSO;
import net.ymate.platform.core.util.DateTimeUtils;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午5:44
 * @version 1.0
 */
public class DefaultSSOToken implements ISSOToken {

    private String id;

    private String uid;

    private String remoteAddr;

    private String userAgent;

    private long lastValidateTime;

    private long createTime;

    private Map<String, String> __attributes;

    public DefaultSSOToken() {
        this.__attributes = new HashMap<String, String>();
    }

    public DefaultSSOToken(String uid) {
        this();
        this.uid = uid;
        this.remoteAddr = WebUtils.getRemoteAddr(WebContext.getRequest());
        this.userAgent = WebContext.getRequest().getHeader("User-Agent");
        this.createTime = System.currentTimeMillis();
    }

    public DefaultSSOToken(String uid, String userAgent, String remoteAddr, long createTime) {
        this();
        this.uid = uid;
        this.remoteAddr = StringUtils.defaultIfBlank(remoteAddr, WebUtils.getRemoteAddr(WebContext.getRequest()));
        this.userAgent = StringUtils.defaultIfBlank(userAgent, WebContext.getRequest().getHeader("User-Agent"));
        this.createTime = createTime > 0 ? createTime : System.currentTimeMillis();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public long getLastValidateTime() {
        return lastValidateTime;
    }

    public void setLastValidateTime(long lastValidateTime) {
        this.lastValidateTime = lastValidateTime;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean hasAttribute(String name) {
        return __attributes.containsKey(name);
    }

    @Override
    public String getAttribute(String name) {
        return __attributes.get(name);
    }

    @Override
    public ISSOToken addAttribute(String name, String value) {
        __attributes.put(name, value);
        return this;
    }

    @Override
    public Map<String, String> getAttributes() {
        return __attributes;
    }

    public ISSOToken build() {
        this.id = __doCreateSignature();
        return this;
    }

    private String __doCreateSignature() {
        return DigestUtils.md5Hex(uid + userAgent + WebContext.getContext().getOwner().getModuleCfg().getCookieAuthKey());
    }

    @Override
    public boolean verified() {
        // 验证客户端与服务端令牌签名是否匹配
        return StringUtils.equals(id, __doCreateSignature());
    }

    @Override
    public boolean timeout() {
        int _maxage = SSO.get().getModuleCfg().getTokenMaxage();
        return _maxage > 0 && System.currentTimeMillis() - this.createTime > _maxage * DateTimeUtils.SECOND;
    }

    @Override
    public boolean validationRequired() {
        int _timeInterval = SSO.get().getModuleCfg().getTokenValidateTimeInterval();
        return _timeInterval <= 0 || System.currentTimeMillis() - this.lastValidateTime > _timeInterval * DateTimeUtils.SECOND;
    }

    @Override
    public ISSOToken updateLastValidateTime() {
        lastValidateTime = System.currentTimeMillis();
        return this;
    }

    @Override
    public UserSessionBean bindUserSessionBean() {
        return UserSessionBean
                .createIfNeed(WebContext.getRequest().getSession().getId()).reset()
                .setUid(uid)
                .addAttribute(ISSOToken.class.getName(), this)
                .save();
    }
}
