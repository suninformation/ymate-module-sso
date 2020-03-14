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

import net.ymate.module.sso.ISingleSignOn;
import net.ymate.module.sso.IToken;
import net.ymate.module.sso.ITokenStorageAdapter;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

/**
 * 默认会话数据存储适配器接口实现
 *
 * @author 刘镇 (suninformation@163.com) on 2017/11/29 23:21
 */
public class DefaultTokenStorageAdapter implements ITokenStorageAdapter {

    private ICache tokenCache;

    private boolean initialized;

    @Override
    public void initialize(ISingleSignOn owner) throws Exception {
        if (!initialized) {
            String cacheName = String.format("%s%s", StringUtils.trimToEmpty(owner.getConfig().getCacheNamePrefix()), ISingleSignOn.MODULE_NAME);
            tokenCache = owner.getOwner().getModuleManager().getModule(Caches.class).getConfig().getCacheProvider().getCache(cacheName);
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
            this.initialized = false;
        }
    }

    @Override
    public IToken load(String tokenId) throws Exception {
        if (StringUtils.isNotBlank(tokenId)) {
            IToken token = (IToken) tokenCache.get(tokenId);
            if (token != null && StringUtils.equals(token.getId(), tokenId)) {
                return token;
            }
        }
        return null;
    }

    @Override
    public void saveOrUpdate(IToken token) throws Exception {
        if (token == null) {
            throw new NullArgumentException("token");
        }
        if (StringUtils.isBlank(token.getId())) {
            throw new NullArgumentException("tokenId");
        }
        tokenCache.put(token.getId(), token);
    }

    @Override
    public IToken remove(String tokenId) throws Exception {
        IToken token = load(tokenId);
        remove(token);
        return token;
    }

    @Override
    public void remove(IToken token) throws Exception {
        if (token != null && StringUtils.isNotBlank(token.getId())) {
            tokenCache.remove(token.getId());
        }
    }

    @Override
    public void cleanup(String uid) throws Exception {
    }

    @Override
    public void cleanup() throws Exception {
    }
}
