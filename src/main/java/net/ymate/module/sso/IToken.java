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

import java.io.Serializable;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/01/01 02:38
 */
public interface IToken extends Serializable {

    String PARAM_TOKEN_ID = "token_id";

    String PARAM_UID = "uid";

    String PARAM_REMOTE_ADDR = "remote_addr";

    String PARAM_USER_AGENT = "user_agent";

    String PARAM_SIGN = "sign";

    /**
     * 获取令牌唯一标识
     *
     * @return 返回令牌唯一标识
     */
    String getId();

    /**
     * 获取用户唯一标识
     *
     * @return 返回用户唯一标识
     */
    String getUid();

    /**
     * 获取IP地址
     *
     * @return 返回IP地址
     */
    String getRemoteAddr();

    /**
     * 获取用户代理信息
     *
     * @return 返回用户代理信息
     */
    String getUserAgent();

    /**
     * 获取最后验证时间（毫秒值）
     *
     * @return 返回最后验证时间
     */
    long getLastValidationTime();

    /**
     * 获取令牌创建时间（毫秒值）
     *
     * @return 返回令牌创建时间
     */
    long getCreateTime();

    /**
     * 获取最后活跃时间（毫秒值）
     *
     * @return 返回最后活跃时间
     */
    long getLastActivateTime();

    /**
     * 获取属性映射
     *
     * @return 返回属性映射
     */
    Map<String, Serializable> getAttributes();

    /**
     * 获取指定名称的属性值
     *
     * @param name 属性名称
     * @return 返回属性值
     */
    Serializable getAttribute(String name);

    /**
     * 添加属性
     *
     * @param name  属性名称
     * @param value 属性值
     */
    void addAttribute(String name, Serializable value);

    /**
     * 判断指定名称的属性是否已存在
     *
     * @param name 属性名称
     * @return 返回true表示已存在
     */
    boolean hasAttribute(String name);

    /**
     * 更新验证时间
     */
    void updateValidationTime();

    /**
     * 更新会话最后活跃时间(毫秒)
     */
    void touch();

    /**
     * 更新会话安全确认状态
     */
    void updateConfirmStatus();

    /**
     * 获取最后更新会话安全确认状态时间（毫秒值）
     *
     * @return 返回更新会话安全确认状态时间（毫秒值）
     */
    long getLastConfirmTime();
}
