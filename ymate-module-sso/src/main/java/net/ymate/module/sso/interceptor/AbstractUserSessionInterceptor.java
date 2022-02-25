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
import net.ymate.module.sso.SingleSignOn;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.core.beans.intercept.AbstractInterceptor;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.beans.intercept.InterceptException;
import net.ymate.platform.core.support.ErrorCode;
import net.ymate.platform.webmvc.IWebErrorProcessor;
import net.ymate.platform.webmvc.IWebMvc;
import net.ymate.platform.webmvc.IWebResultBuilder;
import net.ymate.platform.webmvc.RequestMeta;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.impl.DefaultResponseErrorProcessor;
import net.ymate.platform.webmvc.impl.DefaultWebErrorProcessor;
import net.ymate.platform.webmvc.util.WebResult;
import net.ymate.platform.webmvc.util.WebUtils;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/02/16 16:59
 * @since 2.0.0
 */
public abstract class AbstractUserSessionInterceptor extends AbstractInterceptor {

    @Inject
    private SingleSignOn owner;

    protected IView buildReturnView(HttpServletRequest httpServletRequest, ErrorCode errorCode, String redirectUrl, int redirectTimeInterval, boolean observeSilence) {
        IWebMvc owner = WebContext.getContext().getOwner();
        IWebErrorProcessor errorProcessor = owner.getConfig().getErrorProcessor();
        //
        boolean useDefaultRespErr = false;
        RequestMeta requestMeta = WebContext.getContext().getAttribute(RequestMeta.class.getName());
        if (requestMeta != null && DefaultResponseErrorProcessor.class.equals(requestMeta.getErrorProcessor())) {
            useDefaultRespErr = true;
        }
        if (!useDefaultRespErr) {
            String defaultViewFormat = errorProcessor instanceof DefaultWebErrorProcessor ? ((DefaultWebErrorProcessor) errorProcessor).getErrorDefaultViewFormat() : null;
            if (WebUtils.isAjax(httpServletRequest) || WebUtils.isXmlFormat(httpServletRequest) || WebUtils.isJsonFormat(httpServletRequest) || StringUtils.containsAny(defaultViewFormat, Type.Const.FORMAT_JSON, Type.Const.FORMAT_XML)) {
                IWebResultBuilder builder = WebResult.builder(errorCode);
                if (StringUtils.isNotBlank(redirectUrl)) {
                    builder.attr(Type.Const.REDIRECT_URL, redirectUrl);
                }
                return WebResult.formatView(builder.build(), defaultViewFormat);
            }
        }
        if (observeSilence && StringUtils.isNotBlank(redirectUrl)) {
            return View.redirectView(redirectUrl);
        }
        return WebUtils.buildErrorView(owner, errorCode, redirectUrl, redirectTimeInterval);
    }

    protected IToken getCurrentToken() throws Exception {
        return owner.getCurrentToken();
    }

    public SingleSignOn getOwner() {
        return owner;
    }

    @Override
    protected Object after(InterceptContext context) throws InterceptException {
        return null;
    }
}
