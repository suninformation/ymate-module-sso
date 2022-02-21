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

import net.ymate.platform.core.support.IContext;
import net.ymate.platform.core.support.IInitialization;
import net.ymate.platform.webmvc.view.IView;

/**
 * 会话安全确认处理器接口
 *
 * @author 刘镇 (suninformation@163.com) on 2017/04/25 20:00
 */
public interface ITokenConfirmHandler extends IInitialization<ISingleSignOn> {

    /**
     * 处理完成安全确认动作
     *
     * @param context     环境上下文对象
     * @param token       令牌对象
     * @param redirectUrl 重定向URl地址
     * @return 返回处理结果视图
     * @throws Exception 抛出任何可能异常
     */
    IView handle(IContext context, IToken token, String redirectUrl) throws Exception;
}
