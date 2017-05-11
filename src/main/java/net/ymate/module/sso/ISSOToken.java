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

import net.ymate.framework.webmvc.support.UserSessionBean;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午2:38
 * @version 1.0
 */
public interface ISSOToken extends Serializable {

    /**
     * @return 令牌唯一标识
     */
    String getId();

    /**
     * @return 用户标识ID
     */
    String getUid();

    /**
     * @return IP地址
     */
    String getRemoteAddr();

    /**
     * @return 用户代理信息
     */
    String getUserAgent();

    /**
     * @return 令牌创建时间
     */
    long getCreateTime();

    /**
     * @param name 属性名称
     * @return 获取指定名称的属性值
     */
    String getAttribute(String name);

    /**
     * @param name  属性名称
     * @param value 属性值
     * @return 添加属性并返回当前当前令牌对象
     */
    ISSOToken addAttribute(String name, String value);

    /**
     * @return 获取属性映射
     */
    Map<String, String> getAttributes();

    /**
     * @return 验证令牌数据有效性
     */
    boolean verified();

    /**
     * @return 检查是否已过期
     */
    boolean timeout();

    /**
     * @return 将当前令牌与用户会话绑定，若会话不存在则创建之
     */
    UserSessionBean bindUserSessionBean();
}
