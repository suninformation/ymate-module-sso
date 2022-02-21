/*
 * Copyright 2007-2021 the original author or authors.
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
import net.ymate.module.sso.ITokenBuilder;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/12/14 4:54 下午
 * @since 2.0.0
 */
public class DefaultTokenBuilder implements ITokenBuilder {

    protected final DefaultToken token = new DefaultToken();

    @Override
    public ITokenBuilder id(String id) {
        token.setId(id);
        return this;
    }

    @Override
    public ITokenBuilder uid(String uid) {
        token.setUid(uid);
        return this;
    }

    @Override
    public ITokenBuilder remoteAddr(String remoteAddr) {
        token.setRemoteAddr(remoteAddr);
        return this;
    }

    @Override
    public ITokenBuilder userAgent(String userAgent) {
        token.setUserAgent(userAgent);
        return this;
    }

    @Override
    public ITokenBuilder lastValidationTime(long lastValidationTime) {
        token.setLastValidationTime(lastValidationTime);
        return this;
    }

    @Override
    public ITokenBuilder createTime(long createTime) {
        token.setCreateTime(createTime);
        return this;
    }

    @Override
    public ITokenBuilder expirationTime(long expirationTime) {
        token.setExpirationTime(expirationTime);
        return this;
    }

    @Override
    public IToken build() {
        return token;
    }
}
