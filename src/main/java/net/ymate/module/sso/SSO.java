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

import net.ymate.framework.webmvc.support.UserSessionBean;
import net.ymate.module.sso.impl.DefaultModuleCfg;
import net.ymate.module.sso.impl.DefaultSSOToken;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 上午 03:19
 * @version 1.0
 */
@Module
public class SSO implements IModule, ISSO {

    private static final Log _LOG = LogFactory.getLog(SSO.class);

    public static final Version VERSION = new Version(1, 0, 0, SSO.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile ISSO __instance;

    private YMP __owner;

    private ISSOModuleCfg __moduleCfg;

    private boolean __inited;

    public static ISSO get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(SSO.class);
                }
            }
        }
        return __instance;
    }

    public String getName() {
        return ISSO.MODULE_NAME;
    }

    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-sso-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            //
            __moduleCfg.getTokenAdapter().init(this);
            if (__moduleCfg.getTokenStorageAdapter() != null) {
                __moduleCfg.getTokenStorageAdapter().init(this);
            }
            //
            __inited = true;
        }
    }

    public boolean isInited() {
        return __inited;
    }

    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg.getTokenAdapter().destroy();
            if (__moduleCfg.getTokenStorageAdapter() != null) {
                __moduleCfg.getTokenStorageAdapter().destroy();
            }
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    public YMP getOwner() {
        return __owner;
    }

    public ISSOModuleCfg getModuleCfg() {
        return __moduleCfg;
    }

    public ISSOToken currentToken() {
        UserSessionBean _sessionBean = UserSessionBean.current();
        if (_sessionBean != null) {
            return _sessionBean.getAttribute(ISSOToken.class.getName());
        }
        return null;
    }

    public ISSOToken createToken(String uid) throws Exception {
        ISSOToken _token = new DefaultSSOToken(uid).build();
        //
        __moduleCfg.getTokenStorageAdapter().saveOrUpdate(_token);
        __moduleCfg.getTokenAdapter().setToken(_token);
        //
        _token.bindUserSessionBean();
        //
        return _token;
    }
}
