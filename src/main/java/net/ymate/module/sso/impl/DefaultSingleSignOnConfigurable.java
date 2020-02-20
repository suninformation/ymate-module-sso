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
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurable;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/02/16 11:43
 * @since 2.0.0
 */
public final class DefaultSingleSignOnConfigurable extends DefaultModuleConfigurable {

    public static Builder builder() {
        return new Builder();
    }

    private DefaultSingleSignOnConfigurable() {
        super(ISingleSignOn.MODULE_NAME);
    }

    public static final class Builder {

        private final DefaultSingleSignOnConfigurable configurable = new DefaultSingleSignOnConfigurable();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            configurable.addConfig(ISingleSignOnConfig.ENABLED, String.valueOf(enabled));
            return this;
        }

        public Builder tokenCookieName(String tokenCookieName) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_COOKIE_NAME, tokenCookieName);
            return this;
        }

        public Builder tokenHeaderName(String tokenHeaderName) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_HEADER_NAME, tokenHeaderName);
            return this;
        }

        public Builder tokenParamName(String tokenParamName) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_PARAM_NAME, tokenParamName);
            return this;
        }

        public Builder tokenMaxAge(int tokenMaxAge) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_MAX_AGE, String.valueOf(tokenMaxAge));
            return this;
        }

        public Builder tokenValidationTimeInterval(int tokenValidationTimeInterval) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_VALIDATION_TIME_INTERVAL, String.valueOf(tokenValidationTimeInterval));
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            configurable.addConfig(ISingleSignOnConfig.CACHE_NAME_PREFIX, cacheNamePrefix);
            return this;
        }

        public Builder multiSessionEnabled(boolean multiSessionEnabled) {
            configurable.addConfig(ISingleSignOnConfig.MULTI_SESSION_ENABLED, String.valueOf(multiSessionEnabled));
            return this;
        }

        public Builder multiSessionMaxCount(boolean multiSessionMaxCount) {
            configurable.addConfig(ISingleSignOnConfig.MULTI_SESSION_MAX_COUNT, String.valueOf(multiSessionMaxCount));
            return this;
        }

        public Builder ipCheckEnabled(boolean ipCheckEnabled) {
            configurable.addConfig(ISingleSignOnConfig.IP_CHECK_ENABLED, String.valueOf(ipCheckEnabled));
            return this;
        }

        public Builder clientMode(boolean clientMode) {
            configurable.addConfig(ISingleSignOnConfig.CLIENT_MODE, String.valueOf(clientMode));
            return this;
        }

        public Builder serviceAuthKey(String serviceAuthKey) {
            configurable.addConfig(ISingleSignOnConfig.SERVICE_AUTH_KEY, serviceAuthKey);
            return this;
        }

        public Builder serviceBaseUrl(String serviceBaseUrl) {
            configurable.addConfig(ISingleSignOnConfig.SERVICE_BASE_URL, serviceBaseUrl);
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            configurable.addConfig(ISingleSignOnConfig.SERVICE_PREFIX, servicePrefix);
            return this;
        }

        public Builder generalAuthEnabled(boolean generalAuthEnabled) {
            configurable.addConfig(ISingleSignOnConfig.GENERAL_AUTH_ENABLED, String.valueOf(generalAuthEnabled));
            return this;
        }

        public Builder tokenAdapterClass(Class<? extends ITokenAdapter> tokenAdapterClass) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_ADAPTER_CLASS, tokenAdapterClass.getName());
            return this;
        }

        public Builder tokenStorageAdapterClass(Class<? extends ITokenStorageAdapter> tokenStorageAdapterClass) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_STORAGE_ADAPTER_CLASS, tokenStorageAdapterClass.getName());
            return this;
        }

        public Builder tokenAttributeAdapterClass(Class<? extends ITokenAttributeAdapter> tokenAttributeAdapterClass) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_ATTRIBUTE_ADAPTER_CLASS, tokenAttributeAdapterClass.getName());
            return this;
        }

        public Builder tokenConfirmEnabled(boolean tokenConfirmEnabled) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_CONFIRM_ENABLED, String.valueOf(tokenConfirmEnabled));
            return this;
        }

        public Builder tokenConfirmHandlerClass(Class<? extends ITokenConfirmHandler> tokenConfirmHandlerClass) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_CONFIRM_HANDLER_CLASS, tokenConfirmHandlerClass.getName());
            return this;
        }

        public Builder tokenConfirmRedirectUrl(String tokenConfirmRedirectUrl) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_CONFIRM_REDIRECT_URL, tokenConfirmRedirectUrl);
            return this;
        }

        public Builder tokenConfirmTimeout(int tokenConfirmTimeout) {
            configurable.addConfig(ISingleSignOnConfig.TOKEN_CONFIRM_TIMEOUT, String.valueOf(tokenConfirmTimeout));
            return this;
        }

        public IModuleConfigurer build() {
            return configurable.toModuleConfigurer();
        }
    }
}