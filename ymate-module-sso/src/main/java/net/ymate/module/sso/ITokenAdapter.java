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
 * @author 刘镇 (suninformation@163.com) on 2017/01/01 02:57
 */
public interface ITokenAdapter extends IInitialization<ISingleSignOn>, IDestroyable {

    /**
     * 生成令牌唯一标识
     *
     * @return 返回生成的令牌唯一标识
     */
    String generateTokenKey();

    /**
     * 验证令牌是否有效
     *
     * @param token 令牌对象
     * @return 返回true表示有效
     * @throws Exception 可能产生的任何异常
     */
    boolean validateToken(IToken token) throws Exception;

    /**
     * 从请求中获取令牌对象, 若不存在则返回null
     *
     * @return 返回令牌对象
     */
    IToken getToken();

    /**
     * 设置令牌
     *
     * @param token 令牌对象
     * @return 返回令牌序列化字符串
     * @throws Exception 可能产生的任何异常
     */
    String setToken(IToken token) throws Exception;

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
    String encryptToken(IToken token) throws Exception;

    /**
     * 对加密的令牌序列化串进行解密
     *
     * @param tokenStr 加密的令牌序列化串
     * @return 返回解密后的令牌对象
     * @throws Exception 可能产生的任何异常
     */
    IToken decryptToken(String tokenStr) throws Exception;
}
