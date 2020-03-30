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

import net.ymate.module.sso.IToken;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/01/01 05:44
 */
public class DefaultToken implements IToken {

    private String id;

    private String uid;

    private String remoteAddr;

    private String userAgent;

    private long lastValidationTime;

    private long createTime;

    private long lastActivateTime;

    private long lastConfirmTime;

    private final Map<String, Serializable> attributes = new HashMap<>();

    public DefaultToken() {
    }

    public DefaultToken(String uid, String remoteAddr, String userAgent, long createTime) {
        this.uid = uid;
        this.remoteAddr = remoteAddr;
        this.userAgent = userAgent;
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
    public long getLastValidationTime() {
        return lastValidationTime;
    }

    public void setLastValidationTime(long lastValidationTime) {
        this.lastValidationTime = lastValidationTime;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public long getLastActivateTime() {
        return lastActivateTime;
    }

    @Override
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    @Override
    public Serializable getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void addAttribute(String name, Serializable value) {
        attributes.put(name, value);
    }

    @Override
    public Map<String, Serializable> getAttributes() {
        return attributes;
    }

    @Override
    public void updateValidationTime() {
        this.lastValidationTime = System.currentTimeMillis();
    }

    @Override
    public void touch() {
        this.lastActivateTime = System.currentTimeMillis();
    }

    @Override
    public void updateConfirmStatus() {
        this.lastConfirmTime = System.currentTimeMillis();
    }

    @Override
    public long getLastConfirmTime() {
        return lastConfirmTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("uid", uid)
                .append("remoteAddr", remoteAddr)
                .append("userAgent", userAgent)
                .append("lastValidationTime", lastValidationTime)
                .append("createTime", createTime)
                .append("lastActivateTime", lastActivateTime)
                .append("lastConfirmTime", lastConfirmTime)
                .append("attributes", attributes)
                .toString();
    }
}
