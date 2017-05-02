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
import net.ymate.module.sso.ISSOModuleCfg;
import net.ymate.module.sso.ISSOTokenAdapter;
import net.ymate.module.sso.ISSOTokenStorageAdapter;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.util.ClassUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 上午 03:19
 * @version 1.0
 */
public class DefaultModuleCfg implements ISSOModuleCfg {

    private String __tokenCookieName;

    private String __tokenHeaderName;

    private int __tokenMaxage;

    private String __cacheNamePrefix;

    private boolean __multiSessionEnabled;

    private boolean __ipCheckEnabled;

    private ISSOTokenAdapter __tokenApater;

    private ISSOTokenStorageAdapter __tokenStorageAdapter;

    public DefaultModuleCfg(YMP owner) {
        Map<String, String> _moduleCfgs = owner.getConfig().getModuleConfigs(ISSO.MODULE_NAME);
        //
        __tokenCookieName = StringUtils.defaultIfBlank(_moduleCfgs.get("token_cookie_name"), ISSO.MODULE_NAME + "_token");
        //
        __tokenHeaderName = StringUtils.defaultIfBlank(_moduleCfgs.get("token_header_name"), "X-ModuleSSO-Token");
        //
        __tokenMaxage = BlurObject.bind(_moduleCfgs.get("token_maxage")).toIntValue();
        //
        __cacheNamePrefix = StringUtils.trimToEmpty(_moduleCfgs.get("cache_name_prefix"));
        //
        __multiSessionEnabled = BlurObject.bind(_moduleCfgs.get("multi_session_enabled")).toBooleanValue();
        //
        __ipCheckEnabled = BlurObject.bind(_moduleCfgs.get("ip_check_enabled")).toBooleanValue();
        //
        __tokenApater = ClassUtils.impl(_moduleCfgs.get("token_adapter_class"), ISSOTokenAdapter.class, getClass());
        if (__tokenApater == null) {
            __tokenApater = new DefaultSSOTokenAdapter();
        }
        //
        __tokenStorageAdapter = ClassUtils.impl(_moduleCfgs.get("storage_adapter_class"), ISSOTokenStorageAdapter.class, getClass());
    }

    public String getTokenCookieName() {
        return __tokenCookieName;
    }

    public String getTokenHeaderName() {
        return __tokenHeaderName;
    }

    public int getTokenMaxage() {
        return __tokenMaxage;
    }

    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    public boolean isMultiSessionEnabled() {
        return __multiSessionEnabled;
    }

    public boolean isIpCheckEnabled() {
        return __ipCheckEnabled;
    }

    public ISSOTokenAdapter getTokenAdapter() {
        return __tokenApater;
    }

    public ISSOTokenStorageAdapter getTokenStorageAdapter() {
        return __tokenStorageAdapter;
    }
}