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
 * @author 刘镇 (suninformation@163.com) on 17/1/1 上午2:57
 * @version 1.0
 */
public interface ISSOTokenAdapter {

    /**
     * 初始化令牌适配器
     *
     * @param owner 所属模块管理器实例
     * @throws Exception 可能产生的任何异常
     */
    void init(ISSO owner) throws Exception;

    void destroy() throws Exception;

    /**
     * @return 生成令牌唯一标识KEY
     */
    String generateTokenKey();

    /**
     * 从请求中获取令牌对象
     *
     * @return 返回令牌对象, 若不存在则返回null
     */
    ISSOToken getToken();

    /**
     * 设置令牌
     *
     * @param token 令牌对象
     * @throws Exception 可能产生的任何异常
     */
    void setToken(ISSOToken token) throws Exception;

    /**
     * 清理令牌
     */
    void cleanToken();

    /**
     * 对令牌加密
     *
     * @param token 令牌对象
     * @return 返回加密后的令牌序列化字符串
     * @throws Exception 可能产生的任何异常
     */
    String encryptToken(ISSOToken token) throws Exception;

    /**
     * 对加密的令牌序列化串进行解密
     *
     * @param tokenSeriStr 加密的令牌序列化串
     * @return 返回解密后的令牌对象
     * @throws Exception 可能产生的任何异常
     */
    ISSOToken decryptToken(String tokenSeriStr) throws Exception;
}
