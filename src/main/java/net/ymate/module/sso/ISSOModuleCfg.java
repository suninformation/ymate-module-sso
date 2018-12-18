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
package net.ymate.module.sso;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 上午 03:19
 * @version 1.0
 */
public interface ISSOModuleCfg {

    String TOKEN_COOKIE_NAME = "token_cookie_name";

    String TOKEN_HEADER_NAME = "token_header_name";

    String TOKEN_PARAM_NAME = "token_param_name";

    String TOKEN_MAXAGE = "token_maxage";

    String TOKEN_VALIDATE_TIME_INTERVAL = "token_validate_time_interval";

    String CACHE_NAME_PREFIX = "cache_name_prefix";

    String MULTI_SESSION_ENABLED = "multi_session_enabled";

    String IP_CHECK_ENABLED = "ip_check_enabled";

    String CLIENT_MODE = "client_mode";

    String SERVICE_AUTH_KEY = "service_auth_key";

    String SERVICE_BASE_URL = "service_base_url";

    String TOKEN_ADAPTER_CLASS = "token_adapter_class";

    String STORAGE_ADAPTER_CLASS = "storage_adapter_class";

    String ATTRIBUTE_ADAPTER_CLASS = "attribute_adapter_class";

    /**
     * @return 令牌存储在Cookie中的名称, 默认为module.sso_token
     */
    String getTokenCookieName();

    /**
     * @return 令牌存储在请求头中的名称, 默认为X-ModuleSSO-Token
     */
    String getTokenHeaderName();

    /**
     * @return 令牌URL参数名称, 默认为token
     */
    String getTokenParamName();

    /**
     * @return 令牌生命周期(秒)
     */
    int getTokenMaxage();

    /**
     * @return 令牌有效性验证的时间间隔(秒), 默认值: 0
     */
    int getTokenValidateTimeInterval();

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String getCacheNamePrefix();

    /**
     * @return 开启多会话模式(即同一账号允许多处登录), 默认为false
     */
    boolean isMultiSessionEnabled();

//    /**
//     * @return 同一账号最多会话数量
//     */
//    int getMultiSessionMaxCount();

    /**
     * @return 开启会话的IP地址检查, 默认为false
     */
    boolean isIpCheckEnabled();

    /**
     * @return 是否为客户端模式, 默认为false
     */
    boolean isClientMode();

    /**
     * @return 指定服务端基准URL路径(若客户端模式开启时则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/service/, 默认值: 空
     */
    String getServiceBaseUrl();

    /**
     * @return 客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
     */
    String getServiceAuthKey();

    /**
     * @return 返回令牌分析适配器接口实现
     */
    ISSOTokenAdapter getTokenAdapter();

    /**
     * @return 返回令牌存储适配器接口实现
     */
    ISSOTokenStorageAdapter getTokenStorageAdapter();

    /**
     * @return 返回令牌自定义属性加载适配器接口实现
     */
    ISSOTokenAttributeAdapter getTokenAttributeAdapter();
}