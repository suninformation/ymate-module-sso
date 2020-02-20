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
import net.ymate.platform.commons.util.ExpressionUtils;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.beans.intercept.InterceptException;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.WebErrorCode;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 检查当前用户会话是否有效
 *
 * @author 刘镇 (suninformation@163.com) on 2016/03/12 21:54
 */
public final class UserSessionCheckInterceptor extends AbstractUserSessionInterceptor {

    @Override
    protected Object before(InterceptContext context) throws InterceptException {
        try {
            IToken token = getCurrentToken();
            if (token == null) {
                HttpServletRequest httpServletRequest = WebContext.getRequest();
                //
                UserSessionCheck sessionCheckAnn = findInterceptAnnotation(context, UserSessionCheck.class);
                String redirectUrl = sessionCheckAnn != null ? sessionCheckAnn.redirectUrl() : null;
                if (StringUtils.isNotBlank(redirectUrl)) {
                    redirectUrl = ExpressionUtils.bind(WebUtils.getContextParamValue(context, Type.Const.REDIRECT_URL, redirectUrl))
                            .set(Type.Const.REDIRECT_URL, WebUtils.appendQueryStr(httpServletRequest, true)).getResult();
                }
                return buildReturnView(httpServletRequest, WebErrorCode.userSessionInvalidOrTimeout(), redirectUrl, sessionCheckAnn != null ? sessionCheckAnn.timeInterval() : 0, sessionCheckAnn != null && sessionCheckAnn.observeSilence());
            } else {
                // 更新会话最后活动时间
                token.touch();
            }
        } catch (Exception e) {
            throw new InterceptException(e.getMessage(), e);
        }
        return null;
    }
}
