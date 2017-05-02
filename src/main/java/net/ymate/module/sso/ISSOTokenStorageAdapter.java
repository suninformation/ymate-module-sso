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
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午3:06
 * @version 1.0
 */
public interface ISSOTokenStorageAdapter {

    /**
     * 初始化令牌存储适配器
     *
     * @param owner 所属模块管理器实例
     * @throws Exception 可能产生的任何异常
     */
    void init(ISSO owner) throws Exception;

    void destroy() throws Exception;

    /**
     * 读取令牌数据
     *
     * @param uid     用户标识ID
     * @param tokenId 令牌唯一标识
     * @return 返回令牌对象, 若不存在则返回null
     * @throws Exception 可能产生的任何异常
     */
    ISSOToken load(String uid, String tokenId) throws Exception;

    /**
     * 保存或更新令牌数据
     *
     * @param token 令牌对象
     * @throws Exception 可能产生的任何异常
     */
    void saveOrUpdate(ISSOToken token) throws Exception;

    /**
     * 删除令牌数据
     *
     * @param uid     用户标识ID
     * @param tokenId 令牌唯一标识
     * @return 返回被删除的令牌对象, 若不存在或删除失败则返回null
     * @throws Exception 可能产生的任何异常
     */
    ISSOToken remove(String uid, String tokenId) throws Exception;

    /**
     * 清理已过期的令牌
     *
     * @param uid 用户标识ID
     * @throws Exception 可能产生的任何异常
     */
    void cleanup(String uid) throws Exception;
}
