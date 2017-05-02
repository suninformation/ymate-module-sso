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

import net.ymate.platform.core.YMP;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 上午 03:19
 * @version 1.0
 */
public interface ISSO {

    String MODULE_NAME = "module.sso";

    /**
     * @return 返回所属YMP框架管理器实例
     */
    YMP getOwner();

    /**
     * @return 返回模块配置对象
     */
    ISSOModuleCfg getModuleCfg();

    /**
     * @return 返回模块是否已初始化
     */
    boolean isInited();

    /**
     * @return 尝试从当前会话中获取令牌，若不存在则返回null
     */
    ISSOToken currentToken();

    /**
     * @param uid 用户唯一标识
     * @return 创建令牌
     * @throws Exception 可能产生的任何异常
     */
    ISSOToken createToken(String uid) throws Exception;

    /**
     * 常量
     */
    interface Const {

        String SESSION_TOKEN = "session_token";
    }
}