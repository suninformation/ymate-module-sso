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

import net.ymate.platform.core.support.IDestroyable;
import net.ymate.platform.core.support.IInitialization;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/01/01 03:06
 */
public interface ITokenStorageAdapter extends IInitialization<ISingleSignOn>, IDestroyable {

    /**
     * 读取令牌数据
     *
     * @param tokenId 令牌唯一标识
     * @return 返回令牌对象, 若不存在则返回null
     * @throws Exception 可能产生的任何异常
     */
    IToken load(String tokenId) throws Exception;

    /**
     * 保存或更新令牌数据
     *
     * @param token 令牌对象
     * @throws Exception 可能产生的任何异常
     */
    void saveOrUpdate(IToken token) throws Exception;

    /**
     * 删除令牌数据
     *
     * @param tokenId 令牌唯一标识
     * @return 返回被删除的令牌对象, 若不存在或删除失败则返回null
     * @throws Exception 可能产生的任何异常
     */
    IToken remove(String tokenId) throws Exception;

    /**
     * 删除令牌数据
     *
     * @param token 令牌对象
     * @throws Exception 可能产生的任何异常
     */
    void remove(IToken token) throws Exception;

    /**
     * 清理指定用户已过期的令牌
     *
     * @param uid 用户标识ID
     * @throws Exception 可能产生的任何异常
     */
    void cleanup(String uid) throws Exception;

    /**
     * 清理已过期的令牌
     *
     * @throws Exception 可能产生的任何异常
     */
    void cleanup() throws Exception;
}
