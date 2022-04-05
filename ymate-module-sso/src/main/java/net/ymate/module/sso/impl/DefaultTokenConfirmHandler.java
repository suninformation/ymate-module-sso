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
package net.ymate.module.sso.impl;

import net.ymate.module.sso.ISingleSignOn;
import net.ymate.module.sso.ISingleSignOnConfig;
import net.ymate.module.sso.IToken;
import net.ymate.module.sso.ITokenConfirmHandler;
import net.ymate.platform.commons.util.DateTimeUtils;
import net.ymate.platform.commons.util.ExpressionUtils;
import net.ymate.platform.core.support.IContext;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.exception.UserSessionConfirmationStateException;
import net.ymate.platform.webmvc.util.WebUtils;
import net.ymate.platform.webmvc.view.IView;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/04/25 20:00
 */
public class DefaultTokenConfirmHandler implements ITokenConfirmHandler {

    private ISingleSignOn owner;

    private boolean initialized;

    @Override
    public void initialize(ISingleSignOn owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            this.initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            this.initialized = false;
        }
    }

    protected ISingleSignOn getOwner() {
        return owner;
    }

    @Override
    public IView handle(IContext context, IToken token, String redirectUrl) throws Exception {
        int timeout = owner.getConfig().getTokenConfirmTimeout();
        if (timeout <= 0) {
            timeout = ISingleSignOnConfig.DEFAULT_TOKEN_CONFIRM_TIMEOUT;
        }
        boolean needConfirm = System.currentTimeMillis() - token.getLastConfirmTime() >= DateTimeUtils.MINUTE * timeout;
        if (needConfirm) {
            HttpServletRequest httpServletRequest = WebContext.getRequest();
            //
            if (StringUtils.isNotBlank(redirectUrl)) {
                redirectUrl = ExpressionUtils.bind(redirectUrl).set(Type.Const.REDIRECT_URL, WebUtils.appendQueryStr(httpServletRequest, true)).getResult();
            }
            UserSessionConfirmationStateException confirmException = new UserSessionConfirmationStateException();
            confirmException.setRedirectUrl(redirectUrl);
            throw confirmException;
        }
        return null;
    }
}
