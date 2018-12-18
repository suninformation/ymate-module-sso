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

import net.ymate.module.sso.*;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.support.IConfigReader;
import net.ymate.platform.core.support.impl.MapSafeConfigReader;
import org.apache.commons.lang.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 上午 03:19
 * @version 1.0
 */
public class DefaultSSOModuleCfg implements ISSOModuleCfg {

    private String __tokenCookieName;

    private String __tokenHeaderName;

    private String __tokenParamName;

    private int __tokenMaxage;

    private int __tokenValidateTimeInterval;

    private String __cacheNamePrefix;

    private boolean __multiSessionEnabled;

    private boolean __ipCheckEnabled;

    private boolean __isClientMode;

    private String __serviceBaseUrl;

    private String __serviceAuthKey;

    private ISSOTokenAdapter __tokenAdapter;

    private ISSOTokenStorageAdapter __tokenStorageAdapter;

    private ISSOTokenAttributeAdapter __tokenAttributeAdapter;

    public DefaultSSOModuleCfg(YMP owner) {
        IConfigReader _moduleCfg = MapSafeConfigReader.bind(owner.getConfig().getModuleConfigs(ISSO.MODULE_NAME));
        //
        __tokenCookieName = _moduleCfg.getString(TOKEN_COOKIE_NAME, ISSO.MODULE_NAME + "_token");
        //
        __tokenHeaderName = _moduleCfg.getString(TOKEN_HEADER_NAME, "X-ModuleSSO-Token");
        //
        __tokenParamName = _moduleCfg.getString(TOKEN_PARAM_NAME, "token");
        //
        __tokenMaxage = _moduleCfg.getInt(TOKEN_MAXAGE);
        //
        __tokenValidateTimeInterval = _moduleCfg.getInt(TOKEN_VALIDATE_TIME_INTERVAL);
        //
        __cacheNamePrefix = _moduleCfg.getString(CACHE_NAME_PREFIX);
        //
        __multiSessionEnabled = _moduleCfg.getBoolean(MULTI_SESSION_ENABLED);
        //
        __ipCheckEnabled = _moduleCfg.getBoolean(IP_CHECK_ENABLED);
        //
        __isClientMode = _moduleCfg.getBoolean(CLIENT_MODE);
        //
        __serviceAuthKey = _moduleCfg.getString(SERVICE_AUTH_KEY);
        //
        if (__isClientMode) {
            __serviceBaseUrl = StringUtils.trimToNull(_moduleCfg.getString(SERVICE_BASE_URL));
            if (__serviceBaseUrl != null) {
                if (!StringUtils.startsWithIgnoreCase(__serviceBaseUrl, "http://") &&
                        !StringUtils.startsWithIgnoreCase(__serviceBaseUrl, "https://")) {
                    throw new IllegalArgumentException("The parameter " + SERVICE_BASE_URL + " is invalid");
                } else if (!StringUtils.endsWith(__serviceBaseUrl, "/")) {
                    __serviceBaseUrl = __serviceBaseUrl + "/";
                }
            }
        }
        //
        __tokenAdapter = _moduleCfg.getClassImpl(TOKEN_ADAPTER_CLASS, ISSOTokenAdapter.class);
        if (__tokenAdapter == null) {
            __tokenAdapter = new DefaultSSOTokenAdapter();
        }
        //
        __tokenStorageAdapter = _moduleCfg.getClassImpl(STORAGE_ADAPTER_CLASS, ISSOTokenStorageAdapter.class);
        if (!__isClientMode && __tokenStorageAdapter == null) {
            throw new IllegalArgumentException("The parameter " + STORAGE_ADAPTER_CLASS + " is invalid");
        }
        //
        if (!__isClientMode) {
            __tokenAttributeAdapter = _moduleCfg.getClassImpl(ATTRIBUTE_ADAPTER_CLASS, ISSOTokenAttributeAdapter.class);
        }
    }

    @Override
    public String getTokenCookieName() {
        return __tokenCookieName;
    }

    @Override
    public String getTokenHeaderName() {
        return __tokenHeaderName;
    }

    @Override
    public String getTokenParamName() {
        return __tokenParamName;
    }

    @Override
    public int getTokenMaxage() {
        return __tokenMaxage;
    }

    @Override
    public int getTokenValidateTimeInterval() {
        return __tokenValidateTimeInterval;
    }

    @Override
    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    @Override
    public boolean isMultiSessionEnabled() {
        return __multiSessionEnabled;
    }

    @Override
    public boolean isIpCheckEnabled() {
        return __ipCheckEnabled;
    }

    @Override
    public boolean isClientMode() {
        return __isClientMode;
    }

    @Override
    public String getServiceBaseUrl() {
        return __serviceBaseUrl;
    }

    @Override
    public String getServiceAuthKey() {
        return __serviceAuthKey;
    }

    @Override
    public ISSOTokenAdapter getTokenAdapter() {
        return __tokenAdapter;
    }

    @Override
    public ISSOTokenStorageAdapter getTokenStorageAdapter() {
        return __tokenStorageAdapter;
    }

    @Override
    public ISSOTokenAttributeAdapter getTokenAttributeAdapter() {
        return __tokenAttributeAdapter;
    }
}