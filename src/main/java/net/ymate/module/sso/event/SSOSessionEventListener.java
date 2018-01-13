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
package net.ymate.module.sso.event;

import net.ymate.framework.webmvc.support.UserSessionBean;
import net.ymate.module.sso.ISSOToken;
import net.ymate.module.sso.SSO;
import net.ymate.platform.core.event.Events;
import net.ymate.platform.core.event.IEventListener;
import net.ymate.platform.core.event.IEventRegister;
import net.ymate.platform.core.event.annotation.EventRegister;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.webmvc.WebEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSessionEvent;

/**
 * 会话事件监听器
 *
 * @author 刘镇 (suninformation@163.com) on 16/12/3 上午3:35
 * @version 1.0
 */
@EventRegister
public class SSOSessionEventListener implements IEventRegister {

    // TODO 需要完善在线用户数及对应的用户Id收集

    private static final Log _LOG = LogFactory.getLog(SSOSessionEventListener.class);

    @Override
    public void register(Events events) throws Exception {
        events.registerListener(Events.MODE.NORMAL, WebEvent.class, new IEventListener<WebEvent>() {
            @Override
            public boolean handle(WebEvent context) {
                switch (context.getEventName()) {
                    case SESSION_DESTROYED:
                        HttpSessionEvent _source = context.getEventSource();
                        UserSessionBean _sessionBean = (UserSessionBean) _source.getSession().getAttribute(UserSessionBean.class.getName());
                        if (_sessionBean != null) {
                            ISSOToken _token = _sessionBean.getAttribute(ISSOToken.class.getName());
                            if (_token != null/* && _token.timeout()*/) {
                                try {
                                    SSO.get().getModuleCfg().getTokenStorageAdapter().cleanup(_token.getUid());
                                } catch (Exception e) {
                                    _LOG.warn("An exception occurred while cleaning token for user '" + _token.getUid() + "'", RuntimeUtils.unwrapThrow(e));
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
