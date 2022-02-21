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
package net.ymate.module.sso.interceptor;

import net.ymate.module.sso.ISingleSignOnConfig;
import net.ymate.module.sso.IToken;
import net.ymate.module.sso.ITokenConfirmHandler;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.beans.intercept.InterceptException;
import org.apache.commons.lang3.StringUtils;

/**
 * 在当前用户会话有效的前提下要求再次确认用户密码以保证操作的安全性, 该拦截器用于验证安全确认视图返回的数据的合法性
 *
 * @author 刘镇 (suninformation@163.com) on 2017/04/25 15:53
 */
public final class UserSessionConfirmInterceptor extends AbstractUserSessionInterceptor {

    @Override
    protected Object before(InterceptContext context) throws InterceptException {
        if (getOwner().getConfig().isTokenConfirmEnabled()) {
            try {
                IToken token = getCurrentToken();
                if (token != null) {
                    UserSessionConfirm sessionConfirmAnn = findInterceptAnnotation(context, UserSessionConfirm.class);
                    String redirectUrl = StringUtils.defaultIfBlank(getOwner().getConfig().getTokenConfirmRedirectUrl(), sessionConfirmAnn != null ? sessionConfirmAnn.redirectUrl() : null);
                    ITokenConfirmHandler confirmHandler = getOwner().getConfig().getTokenConfirmHandler();
                    return confirmHandler.handle(context, token, StringUtils.defaultIfBlank(redirectUrl, ISingleSignOnConfig.DEFAULT_TOKEN_CONFIRM_REDIRECT_URL));
                }
            } catch (Exception e) {
                throw new InterceptException(e.getMessage(), e);
            }
        }
        return null;
    }
}
