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
import net.ymate.module.sso.annotation.SingleSignOnConf;
import net.ymate.platform.core.configuration.IConfigReader;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 03:19
 */
public final class DefaultSingleSignOnConfig implements ISingleSignOnConfig {

    private boolean enabled = true;

    private String tokenCookieName;

    private String tokenHeaderName;

    private String tokenParamName;

    private int tokenMaxAge;

    private int tokenValidationTimeInterval;

    private String cacheNamePrefix;

    private boolean multiSessionEnabled;

    private int multiSessionMaxCount;

    private boolean ipCheckEnabled;

    private boolean clientMode;

    private String serviceBaseUrl;

    private String serviceAuthKey;

    private String servicePrefix;

    private boolean generalAuthEnabled;

    private ITokenAdapter tokenAdapter;

    private ITokenStorageAdapter tokenStorageAdapter;

    private ITokenAttributeAdapter tokenAttributeAdapter;

    private boolean tokenConfirmEnabled;

    private ITokenConfirmHandler tokenConfirmHandler;

    private String tokenConfirmRedirectUrl;

    private int tokenConfirmTimeout;

    private boolean initialized;

    public static DefaultSingleSignOnConfig defaultConfig() {
        return builder().build();
    }

    public static DefaultSingleSignOnConfig create(IModuleConfigurer moduleConfigurer) {
        return new DefaultSingleSignOnConfig(null, moduleConfigurer);
    }

    public static DefaultSingleSignOnConfig create(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        return new DefaultSingleSignOnConfig(mainClass, moduleConfigurer);
    }

    public static Builder builder() {
        return new Builder();
    }

    private DefaultSingleSignOnConfig() {
    }

    private DefaultSingleSignOnConfig(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        IConfigReader configReader = moduleConfigurer.getConfigReader();
        //
        SingleSignOnConf confAnn = mainClass == null ? null : mainClass.getAnnotation(SingleSignOnConf.class);
        //
        enabled = configReader.getBoolean(ENABLED, confAnn == null || confAnn.enabled());
        if (enabled) {
            tokenCookieName = configReader.getString(TOKEN_COOKIE_NAME, StringUtils.defaultIfBlank(confAnn != null ? confAnn.tokenCookieName() : null, DEFAULT_TOKEN_COOKIE_NAME));
            tokenHeaderName = configReader.getString(TOKEN_HEADER_NAME, StringUtils.defaultIfBlank(confAnn != null ? confAnn.tokenHeaderName() : null, DEFAULT_TOKEN_HEADER_NAME));
            tokenParamName = configReader.getString(TOKEN_PARAM_NAME, StringUtils.defaultIfBlank(confAnn != null ? confAnn.tokenParamName() : null, DEFAULT_TOKEN_NAME));
            tokenMaxAge = configReader.getInt(TOKEN_MAX_AGE, confAnn != null && confAnn.tokenMaxAge() > 0 ? confAnn.tokenMaxAge() : 0);
            tokenValidationTimeInterval = configReader.getInt(TOKEN_VALIDATION_TIME_INTERVAL, confAnn != null && confAnn.tokenValidationTimeInterval() > 0 ? confAnn.tokenValidationTimeInterval() : 0);
            cacheNamePrefix = configReader.getString(CACHE_NAME_PREFIX, confAnn != null ? confAnn.cacheNamePrefix() : null);
            multiSessionEnabled = configReader.getBoolean(MULTI_SESSION_ENABLED, confAnn != null && confAnn.multiSessionEnabled());
            multiSessionMaxCount = configReader.getInt(MULTI_SESSION_MAX_COUNT, confAnn != null && confAnn.multiSessionMaxCount() > 0 ? confAnn.multiSessionMaxCount() : 0);
            ipCheckEnabled = configReader.getBoolean(IP_CHECK_ENABLED, confAnn != null && confAnn.ipCheckEnabled());
            clientMode = configReader.getBoolean(CLIENT_MODE, confAnn != null && confAnn.clientMode());
            tokenAdapter = configReader.getClassImpl(TOKEN_ADAPTER_CLASS, confAnn == null || confAnn.tokenAdapterClass().equals(ITokenAdapter.class) ? null : confAnn.tokenAdapterClass().getName(), ITokenAdapter.class);
            tokenConfirmEnabled = configReader.getBoolean(TOKEN_CONFIRM_ENABLED, confAnn != null && confAnn.tokenConfirmEnabled());
            tokenConfirmHandler = configReader.getClassImpl(TOKEN_CONFIRM_HANDLER_CLASS, confAnn == null || confAnn.tokenConfirmHandlerClass().equals(ITokenConfirmHandler.class) ? null : confAnn.tokenConfirmHandlerClass().getName(), ITokenConfirmHandler.class);
            tokenConfirmRedirectUrl = configReader.getString(TOKEN_CONFIRM_REDIRECT_URL, confAnn != null ? confAnn.tokenConfirmRedirectUrl() : null);
            tokenConfirmTimeout = configReader.getInt(TOKEN_CONFIRM_TIMEOUT, confAnn != null && confAnn.tokenConfirmTimeout() > 0 ? confAnn.tokenConfirmTimeout() : DEFAULT_TOKEN_CONFIRM_TIMEOUT);
            generalAuthEnabled = configReader.getBoolean(GENERAL_AUTH_ENABLED, confAnn != null && confAnn.generalAuthEnabled());
            servicePrefix = configReader.getString(SERVICE_PREFIX, confAnn != null ? confAnn.servicePrefix() : null);
            serviceAuthKey = configReader.getString(SERVICE_AUTH_KEY, confAnn != null ? confAnn.serviceAuthKey() : null);
            if (clientMode) {
                serviceBaseUrl = StringUtils.trimToNull(configReader.getString(SERVICE_BASE_URL, confAnn != null ? confAnn.serviceBaseUrl() : null));
            } else {
                tokenStorageAdapter = configReader.getClassImpl(TOKEN_STORAGE_ADAPTER_CLASS, confAnn == null || confAnn.tokenStorageAdapterClass().equals(ITokenStorageAdapter.class) ? null : confAnn.tokenStorageAdapterClass().getName(), ITokenStorageAdapter.class);
                tokenAttributeAdapter = configReader.getClassImpl(TOKEN_ATTRIBUTE_ADAPTER_CLASS, confAnn == null || confAnn.tokenAttributeAdapterClass().equals(ITokenAttributeAdapter.class) ? null : confAnn.tokenAttributeAdapterClass().getName(), ITokenAttributeAdapter.class);
            }
        }
    }

    @Override
    public void initialize(ISingleSignOn owner) throws Exception {
        if (!initialized) {
            if (enabled) {
                if (tokenAdapter == null) {
                    tokenAdapter = new DefaultTokenAdapter();
                }
                tokenAdapter.initialize(owner);
                //
                if (tokenConfirmEnabled) {
                    if (tokenConfirmHandler == null) {
                        tokenConfirmHandler = new DefaultTokenConfirmHandler();
                    }
                    tokenConfirmHandler.initialize(owner);
                }
                //
                servicePrefix = WebUtils.fixUrl(servicePrefix, false, false);
                //
                if (clientMode) {
                    if (serviceBaseUrl != null) {
                        if (!StringUtils.startsWithIgnoreCase(serviceBaseUrl, Type.Const.HTTP_PREFIX) && !StringUtils.startsWithIgnoreCase(serviceBaseUrl, Type.Const.HTTPS_PREFIX)) {
                            throw new IllegalArgumentException(String.format("The parameter %s is invalid", SERVICE_BASE_URL));
                        } else if (!StringUtils.endsWith(serviceBaseUrl, Type.Const.PATH_SEPARATOR)) {
                            serviceBaseUrl += Type.Const.PATH_SEPARATOR;
                        }
                    }
                } else {
                    if (tokenStorageAdapter == null) {
                        tokenStorageAdapter = new DefaultTokenStorageAdapter();
                    }
                    tokenStorageAdapter.initialize(owner);
                }
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!initialized) {
            this.enabled = enabled;
        }
    }

    @Override
    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public void setTokenCookieName(String tokenCookieName) {
        if (!initialized) {
            this.tokenCookieName = tokenCookieName;
        }
    }

    @Override
    public String getTokenHeaderName() {
        return tokenHeaderName;
    }

    public void setTokenHeaderName(String tokenHeaderName) {
        if (!initialized) {
            this.tokenHeaderName = tokenHeaderName;
        }
    }

    @Override
    public String getTokenParamName() {
        return tokenParamName;
    }

    public void setTokenParamName(String tokenParamName) {
        if (!initialized) {
            this.tokenParamName = tokenParamName;
        }
    }

    @Override
    public int getTokenMaxAge() {
        return tokenMaxAge;
    }

    public void setTokenMaxAge(int tokenMaxAge) {
        if (!initialized) {
            this.tokenMaxAge = tokenMaxAge;
        }
    }

    @Override
    public int getTokenValidationTimeInterval() {
        return tokenValidationTimeInterval;
    }

    public void setTokenValidationTimeInterval(int tokenValidationTimeInterval) {
        if (!initialized) {
            this.tokenValidationTimeInterval = tokenValidationTimeInterval;
        }
    }

    @Override
    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    public void setCacheNamePrefix(String cacheNamePrefix) {
        if (!initialized) {
            this.cacheNamePrefix = cacheNamePrefix;
        }
    }

    @Override
    public boolean isMultiSessionEnabled() {
        return multiSessionEnabled;
    }

    public void setMultiSessionEnabled(boolean multiSessionEnabled) {
        if (!initialized) {
            this.multiSessionEnabled = multiSessionEnabled;
        }
    }

    @Override
    public int getMultiSessionMaxCount() {
        return multiSessionMaxCount;
    }

    public void setMultiSessionMaxCount(int multiSessionMaxCount) {
        if (!initialized) {
            this.multiSessionMaxCount = multiSessionMaxCount;
        }
    }

    @Override
    public boolean isIpCheckEnabled() {
        return ipCheckEnabled;
    }

    public void setIpCheckEnabled(boolean ipCheckEnabled) {
        if (!initialized) {
            this.ipCheckEnabled = ipCheckEnabled;
        }
    }

    @Override
    public boolean isClientMode() {
        return clientMode;
    }

    public void setClientMode(boolean clientMode) {
        if (!initialized) {
            this.clientMode = clientMode;
        }
    }

    @Override
    public String getServiceBaseUrl() {
        return serviceBaseUrl;
    }

    public void setServiceBaseUrl(String serviceBaseUrl) {
        if (!initialized) {
            this.serviceBaseUrl = serviceBaseUrl;
        }
    }

    @Override
    public String getServiceAuthKey() {
        return serviceAuthKey;
    }

    public void setServiceAuthKey(String serviceAuthKey) {
        if (!initialized) {
            this.serviceAuthKey = serviceAuthKey;
        }
    }

    @Override
    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        if (!initialized) {
            this.servicePrefix = servicePrefix;
        }
    }

    @Override
    public boolean isGeneralAuthEnabled() {
        return generalAuthEnabled;
    }

    public void setGeneralAuthEnabled(boolean generalAuthEnabled) {
        if (!initialized) {
            this.generalAuthEnabled = generalAuthEnabled;
        }
    }

    @Override
    public ITokenAdapter getTokenAdapter() {
        return tokenAdapter;
    }

    public void setTokenAdapter(ITokenAdapter tokenAdapter) {
        if (!initialized) {
            this.tokenAdapter = tokenAdapter;
        }
    }

    @Override
    public ITokenStorageAdapter getTokenStorageAdapter() {
        return tokenStorageAdapter;
    }

    public void setTokenStorageAdapter(ITokenStorageAdapter tokenStorageAdapter) {
        if (!initialized) {
            this.tokenStorageAdapter = tokenStorageAdapter;
        }
    }

    @Override
    public ITokenAttributeAdapter getTokenAttributeAdapter() {
        return tokenAttributeAdapter;
    }

    public void setTokenAttributeAdapter(ITokenAttributeAdapter tokenAttributeAdapter) {
        if (!initialized) {
            this.tokenAttributeAdapter = tokenAttributeAdapter;
        }
    }

    @Override
    public boolean isTokenConfirmEnabled() {
        return tokenConfirmEnabled;
    }

    public void setTokenConfirmEnabled(boolean tokenConfirmEnabled) {
        if (!initialized) {
            this.tokenConfirmEnabled = tokenConfirmEnabled;
        }
    }

    @Override
    public ITokenConfirmHandler getTokenConfirmHandler() {
        return tokenConfirmHandler;
    }

    public void setTokenConfirmHandler(ITokenConfirmHandler tokenConfirmHandler) {
        if (!initialized) {
            this.tokenConfirmHandler = tokenConfirmHandler;
        }
    }

    @Override
    public String getTokenConfirmRedirectUrl() {
        return tokenConfirmRedirectUrl;
    }

    public void setTokenConfirmRedirectUrl(String tokenConfirmRedirectUrl) {
        if (!initialized) {
            this.tokenConfirmRedirectUrl = tokenConfirmRedirectUrl;
        }
    }

    @Override
    public int getTokenConfirmTimeout() {
        return tokenConfirmTimeout;
    }

    public void setTokenConfirmTimeout(int tokenConfirmTimeout) {
        if (!initialized) {
            this.tokenConfirmTimeout = tokenConfirmTimeout;
        }
    }

    public static final class Builder {

        private final DefaultSingleSignOnConfig config = new DefaultSingleSignOnConfig();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public Builder tokenCookieName(String tokenCookieName) {
            config.setTokenCookieName(tokenCookieName);
            return this;
        }

        public Builder tokenHeaderName(String tokenHeaderName) {
            config.setTokenHeaderName(tokenHeaderName);
            return this;
        }

        public Builder tokenParamName(String tokenParamName) {
            config.setTokenParamName(tokenParamName);
            return this;
        }

        public Builder tokenMaxAge(int tokenMaxAge) {
            config.setTokenMaxAge(tokenMaxAge);
            return this;
        }

        public Builder tokenValidationTimeInterval(int tokenValidationTimeInterval) {
            config.setTokenValidationTimeInterval(tokenValidationTimeInterval);
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            config.setCacheNamePrefix(cacheNamePrefix);
            return this;
        }

        public Builder multiSessionEnabled(boolean multiSessionEnabled) {
            config.setMultiSessionEnabled(multiSessionEnabled);
            return this;
        }

        public Builder multiSessionMaxCount(int multiSessionMaxCount) {
            config.setMultiSessionMaxCount(multiSessionMaxCount);
            return this;
        }

        public Builder ipCheckEnabled(boolean ipCheckEnabled) {
            config.setIpCheckEnabled(ipCheckEnabled);
            return this;
        }

        public Builder clientMode(boolean clientMode) {
            config.setClientMode(clientMode);
            return this;
        }

        public Builder serviceBaseUrl(String serviceBaseUrl) {
            config.setServiceBaseUrl(serviceBaseUrl);
            return this;
        }

        public Builder serviceAuthKey(String serviceAuthKey) {
            config.setServiceAuthKey(serviceAuthKey);
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            config.setServicePrefix(servicePrefix);
            return this;
        }

        public Builder generalAuthEnabled(boolean generalAuthEnabled) {
            config.setGeneralAuthEnabled(generalAuthEnabled);
            return this;
        }

        public Builder tokenAdapter(ITokenAdapter tokenAdapter) {
            config.setTokenAdapter(tokenAdapter);
            return this;
        }

        public Builder tokenStorageAdapter(ITokenStorageAdapter tokenStorageAdapter) {
            config.setTokenStorageAdapter(tokenStorageAdapter);
            return this;
        }

        public Builder tokenAttributeAdapter(ITokenAttributeAdapter tokenAttributeAdapter) {
            config.setTokenAttributeAdapter(tokenAttributeAdapter);
            return this;
        }

        public Builder tokenConfirmEnabled(boolean tokenConfirmEnabled) {
            config.setTokenConfirmEnabled(tokenConfirmEnabled);
            return this;
        }

        public Builder tokenConfirmHandler(ITokenConfirmHandler tokenConfirmHandler) {
            config.setTokenConfirmHandler(tokenConfirmHandler);
            return this;
        }

        public Builder tokenConfirmRedirectUrl(String tokenConfirmRedirectUrl) {
            config.setTokenConfirmRedirectUrl(tokenConfirmRedirectUrl);
            return this;
        }

        public Builder tokenConfirmTimeout(int tokenConfirmTimeout) {
            config.setTokenConfirmTimeout(tokenConfirmTimeout);
            return this;
        }

        public DefaultSingleSignOnConfig build() {
            return config;
        }
    }
}