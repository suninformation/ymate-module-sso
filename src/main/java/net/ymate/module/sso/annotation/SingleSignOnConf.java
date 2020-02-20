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
package net.ymate.module.sso.annotation;

import net.ymate.module.sso.ITokenAdapter;
import net.ymate.module.sso.ITokenAttributeAdapter;
import net.ymate.module.sso.ITokenConfirmHandler;
import net.ymate.module.sso.ITokenStorageAdapter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/03/11 19:37
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SingleSignOnConf {

    /**
     * @return 模块是否已启用, 默认值: true
     */
    boolean enabled() default true;

    /**
     * @return 令牌存储在Cookie中的名称, 默认为module.sso_token
     */
    String tokenCookieName() default StringUtils.EMPTY;

    /**
     * @return 令牌存储在请求头中的名称, 默认为X-ModuleSSO-Token
     */
    String tokenHeaderName() default StringUtils.EMPTY;

    /**
     * @return 令牌URL参数名称, 默认为token
     */
    String tokenParamName() default StringUtils.EMPTY;

    /**
     * @return 令牌生命周期(秒)
     */
    int tokenMaxAge() default 0;

    /**
     * @return 令牌有效性验证的时间间隔(秒), 默认值: 0
     */
    int tokenValidationTimeInterval() default 0;

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String cacheNamePrefix() default StringUtils.EMPTY;

    /**
     * @return 开启多会话模式(即同一账号允许多处登录), 默认值: false
     */
    boolean multiSessionEnabled() default false;

    /**
     * @return 同一账号最多会话数量，小于等于0表示不限制
     */
    int multiSessionMaxCount() default 0;

    /**
     * @return 开启会话的IP地址检查, 默认为false
     */
    boolean ipCheckEnabled() default false;

    /**
     * @return 是否为客户端模式, 默认为false
     */
    boolean clientMode() default false;

    /**
     * @return 指定服务端基准URL路径(若客户端模式开启时则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/service/, 默认值: 空
     */
    String serviceBaseUrl() default StringUtils.EMPTY;

    /**
     * @return 客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
     */
    String serviceAuthKey() default StringUtils.EMPTY;

    /**
     * @return 服务请求映射前缀(不允许 ' / ' 开始和结束), 默认值: ""
     */
    String servicePrefix() default StringUtils.EMPTY;

    /**
     * @return 是否注册通用令牌验证控制器, 默认值: false
     */
    boolean generalAuthEnabled() default false;

    /**
     * @return 令牌分析适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenAdapter
     */
    Class<? extends ITokenAdapter> tokenAdapterClass() default ITokenAdapter.class;

    /**
     * @return 令牌存储适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenStorageAdapter
     */
    Class<? extends ITokenStorageAdapter> tokenStorageAdapterClass() default ITokenStorageAdapter.class;

    /**
     * @return 令牌自定义属性加载适配器接口实现, 非客户端模式时有效, 默认值: 空
     */
    Class<? extends ITokenAttributeAdapter> tokenAttributeAdapterClass() default ITokenAttributeAdapter.class;

    /**
     * @return 是否开启会话安全确认, 默认值: false
     */
    boolean tokenConfirmEnabled() default false;

    /**
     * @return 会话安全确认处理器
     */
    Class<? extends ITokenConfirmHandler> tokenConfirmHandlerClass() default ITokenConfirmHandler.class;

    /**
     * @return 会话安全确认重定向URL地址, 默认值: confirm?redirect_url=${redirect_url}
     */
    String tokenConfirmRedirectUrl() default StringUtils.EMPTY;

    /**
     * @return 会话安全确认超时时间(分钟), 小于等于0则使用默认值: 30
     */
    int tokenConfirmTimeout() default 0;
}
