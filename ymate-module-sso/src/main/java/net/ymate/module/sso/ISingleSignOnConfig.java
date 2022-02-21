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
package net.ymate.module.sso;

import net.ymate.platform.core.support.IInitialization;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 03:19
 */
public interface ISingleSignOnConfig extends IInitialization<ISingleSignOn> {

    String DEFAULT_TOKEN_NAME = "token";

    String DEFAULT_TOKEN_HEADER_NAME = "X-ModuleSSO-Token";

    String DEFAULT_TOKEN_COOKIE_NAME = String.format("%s_%s", ISingleSignOn.MODULE_NAME, DEFAULT_TOKEN_NAME);

    String DEFAULT_CONTROLLER_MAPPING = "/authorize";

    int DEFAULT_TOKEN_CONFIRM_TIMEOUT = 30;

    String DEFAULT_TOKEN_CONFIRM_REDIRECT_URL = "confirm?redirect_url=${redirect_url}";

    String ENABLED = "enabled";

    String TOKEN_COOKIE_NAME = "token_cookie_name";

    String TOKEN_HEADER_NAME = "token_header_name";

    String TOKEN_PARAM_NAME = "token_param_name";

    String TOKEN_MAX_AGE = "token_max_age";

    String TOKEN_VALIDATION_TIME_INTERVAL = "token_validation_time_interval";

    String CACHE_NAME_PREFIX = "cache_name_prefix";

    String MULTI_SESSION_ENABLED = "multi_session_enabled";

    String MULTI_SESSION_MAX_COUNT = "multi_session_max_count";

    String IP_CHECK_ENABLED = "ip_check_enabled";

    String CLIENT_MODE = "client_mode";

    String SERVICE_AUTH_KEY = "service_auth_key";

    String SERVICE_BASE_URL = "service_base_url";

    String SERVICE_PREFIX = "service_prefix";

    String GENERAL_AUTH_ENABLED = "general_auth_enabled";

    String TOKEN_ADAPTER_CLASS = "token_adapter_class";

    String TOKEN_STORAGE_ADAPTER_CLASS = "token_storage_adapter_class";

    String TOKEN_ATTRIBUTE_ADAPTER_CLASS = "token_attribute_adapter_class";

    String TOKEN_INVALID_REDIRECT_URL = "token_invalid_redirect_url";

    String TOKEN_ALREADY_REDIRECT_URL = "token_already_redirect_url";

    String TOKEN_CONFIRM_ENABLED = "token_confirm_enabled";

    String TOKEN_CONFIRM_HANDLER_CLASS = "token_confirm_handler_class";

    String TOKEN_CONFIRM_REDIRECT_URL = "token_confirm_redirect_url";

    String TOKEN_CONFIRM_TIMEOUT = "token_confirm_timeout";

    /**
     * 模块是否已启用, 默认值: true
     *
     * @return 返回false表示禁用
     */
    boolean isEnabled();

    /**
     * 令牌存储在Cookie中的名称, 默认为module.sso_token
     *
     * @return 返回Cookie名称
     */
    String getTokenCookieName();

    /**
     * 令牌存储在请求头中的名称, 默认为X-ModuleSSO-Token
     *
     * @return 返回请求头名称
     */
    String getTokenHeaderName();

    /**
     * 令牌URL参数名称, 默认为token
     *
     * @return 返回参数名称
     */
    String getTokenParamName();

    /**
     * 令牌生命周期(秒)
     *
     * @return 返回令牌生命周期(秒)
     */
    int getTokenMaxAge();

    /**
     * 令牌有效性验证的时间间隔(秒), 默认值: 0
     *
     * @return 返回时间间隔(秒)
     */
    int getTokenValidationTimeInterval();

    /**
     * 缓存名称前缀, 默认值: ""
     *
     * @return 返回缓存名称前缀
     */
    String getCacheNamePrefix();

    /**
     * 开启多会话模式(即同一账号允许多处登录), 默认值: false
     *
     * @return 返回true表示开启
     */
    boolean isMultiSessionEnabled();

    /**
     * 同一账号最多会话数量，小于等于0表示不限制
     *
     * @return 返回同一账号最多会话数量
     */
    int getMultiSessionMaxCount();

    /**
     * 开启会话的IP地址检查, 默认为false
     *
     * @return 返回true表示开启
     */
    boolean isIpCheckEnabled();

    /**
     * 是否为客户端模式, 默认为false
     *
     * @return 返回true表示当前为客户端模式
     */
    boolean isClientMode();

    /**
     * 指定服务端基准URL路径(若客户端模式开启时则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/service/, 默认值: 空
     *
     * @return 返回服务基准URL请求路径
     */
    String getServiceBaseUrl();

    /**
     * 客户端与服务端之间通讯请求参数签名以及令牌加解密附加密钥, 默认值: ""
     *
     * @return 返回密钥
     */
    String getServiceAuthKey();

    /**
     * 服务请求映射前缀(不允许'/'开始和结束), 默认值: ""
     *
     * @return 返回服务请求映射前缀
     */
    String getServicePrefix();

    /**
     * 是否注册通用令牌验证控制器, 默认值: false
     *
     * @return 返回true表示注册
     */
    boolean isGeneralAuthEnabled();

    /**
     * 令牌分析适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenAdapter
     *
     * @return 返回令牌分析适配器接口实现
     */
    ITokenAdapter getTokenAdapter();

    /**
     * 令牌存储适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenStorageAdapter
     *
     * @return 返回令牌存储适配器接口实现
     */
    ITokenStorageAdapter getTokenStorageAdapter();

    /**
     * 令牌自定义属性加载适配器接口实现, 非客户端模式时有效, 默认值: 空
     *
     * @return 返回令牌自定义属性加载适配器接口实现
     */
    ITokenAttributeAdapter getTokenAttributeAdapter();

    /**
     * 检测令牌无效时重定向URL地址(用于跳转至用户登录页面, 如: login?redirect_url=${redirect_url}), 默认值: 空
     *
     * @return 返回令牌无效时重定向URL地址
     */
    String getTokenInvalidRedirectUrl();

    /**
     * 检测令牌已存在时重定向URL地址(用于登录成功后跳转), 默认值: 空
     *
     * @return 返回令牌已存在时重定向URL地址
     */
    String getTokenAlreadyRedirectUrl();

    /**
     * 是否开启会话安全确认, 默认值: false
     *
     * @return 返回true表示开启
     */
    boolean isTokenConfirmEnabled();

    /**
     * 会话安全确认处理器
     *
     * @return 返回会话安全确认处理器接口实现
     */
    ITokenConfirmHandler getTokenConfirmHandler();

    /**
     * 会话安全确认重定向URL地址, 默认值: confirm?redirect_url=${redirect_url}
     *
     * @return 返回会话安全确认重定向URL地址
     */
    String getTokenConfirmRedirectUrl();

    /**
     * 会话安全确认超时时间(分钟), 小于等于0则使用默认值: 30
     *
     * @return 返回超时时间(分钟)
     */
    int getTokenConfirmTimeout();
}