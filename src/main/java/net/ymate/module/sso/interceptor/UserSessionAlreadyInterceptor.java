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

import net.ymate.module.sso.IToken;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.beans.intercept.InterceptException;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.WebErrorCode;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 已登录用户拦截器
 *
 * @author 刘镇 (suninformation@163.com) on 2016/12/02 01:39
 */
public final class UserSessionAlreadyInterceptor extends AbstractUserSessionInterceptor {

    @Override
    protected Object before(InterceptContext context) throws InterceptException {
        try {
            IToken token = getCurrentToken();
            if (token != null) {
                token.touch();
                HttpServletRequest httpServletRequest = WebContext.getRequest();
                //
                UserSessionAlready sessionAlreadyAnn = findInterceptAnnotation(context, UserSessionAlready.class);
                String redirectUrl = sessionAlreadyAnn != null ? sessionAlreadyAnn.redirectUrl() : null;
                if (StringUtils.isNotBlank(redirectUrl)) {
                    redirectUrl = WebUtils.buildRedirectUrl(context, httpServletRequest, WebUtils.getContextParamValue(context, Type.Const.REDIRECT_URL, redirectUrl), true);
                }
                return buildReturnView(httpServletRequest, WebErrorCode.userSessionAuthorized(), redirectUrl, sessionAlreadyAnn != null ? sessionAlreadyAnn.timeInterval() : 0, sessionAlreadyAnn != null && sessionAlreadyAnn.observeSilence());
            }
        } catch (Exception e) {
            throw new InterceptException(e.getMessage(), e);
        }
        return null;
    }
}
