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

import net.ymate.platform.core.IApplication;
import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.support.IDestroyable;
import net.ymate.platform.core.support.IInitialization;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 03:19
 */
@Ignored
public interface ISingleSignOn extends IInitialization<IApplication>, IDestroyable {

    String MODULE_NAME = "module.sso";

    /**
     * 获取所属应用容器
     *
     * @return 返回所属应用容器实例
     */
    IApplication getOwner();

    /**
     * 获取配置
     *
     * @return 返回配置对象
     */
    ISingleSignOnConfig getConfig();

    /**
     * 尝试从当前会话中获取令牌
     *
     * @return 返回令牌对象
     * @throws Exception 可能产生的任何异常
     */
    IToken getCurrentToken() throws Exception;

    /**
     * 尝试从存储中加载原始令牌
     *
     * @param tokenId 令牌唯一标识
     * @return 返回令牌对象
     * @throws Exception 可能产生的任何异常
     */
    IToken getToken(String tokenId) throws Exception;

    /**
     * 检查是否已过期
     *
     * @param token 令牌对象
     * @return 返回true表示已过期
     */
    boolean isTimeout(IToken token);

    /**
     * 是否需要执行令牌有效性验证
     *
     * @param token 令牌对象
     * @return 返回true表示需要
     */
    boolean isValidationRequired(IToken token);

    /**
     * 创建新令牌（并未设置其生效和持久化）
     *
     * @param uid        用户唯一标识
     * @param remoteAddr IP地址
     * @param userAgent  代理信息
     * @return 返回新创建的令牌对象
     * @throws Exception 可能产生的任何异常
     */
    IToken createToken(String uid, String remoteAddr, String userAgent) throws Exception;

    /**
     * 存储或更新令牌并使其生效
     *
     * @param token 令牌对象
     * @return 返回令牌序列化字符串
     * @throws Exception 可能产生的任何异常
     */
    String saveOrUpdateToken(IToken token) throws Exception;

    /**
     * 存储或更新令牌并使其生效
     *
     * @param token  令牌对象
     * @param cookie 是否设置Cookie值
     * @return 返回令牌序列化字符串
     * @throws Exception 可能产生的任何异常
     */
    String saveOrUpdateToken(IToken token, boolean cookie) throws Exception;

    /**
     * 清理并删除令牌存储
     *
     * @param token 令牌对象
     * @throws Exception 可能产生的任何异常
     */
    void cleanAndRemoveToken(IToken token) throws Exception;

    /**
     * 清理并删除令牌存储
     *
     * @param token  令牌对象
     * @param cookie 是否设置Cookie值
     * @throws Exception 可能产生的任何异常
     */
    void cleanAndRemoveToken(IToken token, boolean cookie) throws Exception;
}