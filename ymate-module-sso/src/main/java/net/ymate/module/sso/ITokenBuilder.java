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
package net.ymate.module.sso;

/**
 * 令牌对象构建器
 *
 * @author 刘镇 (suninformation@163.com) on 2021/12/14 4:41 下午
 * @since 2.0.0
 */
public interface ITokenBuilder {

    /**
     * 设置令牌唯一标识
     *
     * @param id 令牌唯一标识
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder id(String id);

    /**
     * 设置用户唯一标识
     *
     * @param uid 用户唯一标识
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder uid(String uid);

    /**
     * 设置远程IP地址
     *
     * @param remoteAddr 远程IP地址
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder remoteAddr(String remoteAddr);

    /**
     * 设置用户代理信息
     *
     * @param userAgent 用户代理信息
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder userAgent(String userAgent);

    /**
     * 设置最后验证时间
     *
     * @param lastValidationTime 最后验证时间
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder lastValidationTime(long lastValidationTime);

    /**
     * 设置令牌创建时间
     *
     * @param createTime 令牌创建时间
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder createTime(long createTime);

    /**
     * 设置令牌过期时间，小于等于0表示不限制
     *
     * @param expirationTime 令牌过期时间
     * @return 返回当前令牌对象构建器实例
     */
    ITokenBuilder expirationTime(long expirationTime);

    /**
     * 构建令牌对象
     *
     * @return 返回构建的令牌对象
     */
    IToken build();
}
