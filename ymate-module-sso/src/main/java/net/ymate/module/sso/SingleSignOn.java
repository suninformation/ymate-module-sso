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

import net.ymate.module.sso.controller.GeneralAuthController;
import net.ymate.module.sso.controller.ServerAuthController;
import net.ymate.module.sso.impl.DefaultSingleSignOnConfig;
import net.ymate.module.sso.impl.DefaultTokenBuilder;
import net.ymate.module.sso.interceptor.*;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.commons.util.DateTimeUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.*;
import net.ymate.platform.core.beans.BeanMeta;
import net.ymate.platform.core.beans.IBeanFactory;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.core.beans.intercept.InterceptSettings;
import net.ymate.platform.core.event.Events;
import net.ymate.platform.core.event.IEventListener;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurer;
import net.ymate.platform.webmvc.IWebMvc;
import net.ymate.platform.webmvc.WebEvent;
import net.ymate.platform.webmvc.WebMVC;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSessionEvent;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/02/26 03:19
 */
public final class SingleSignOn implements IModule, ISingleSignOn {

    private static final Log LOG = LogFactory.getLog(SingleSignOn.class);

    private static volatile ISingleSignOn instance;

    private IApplication owner;

    private ISingleSignOnConfig config;

    private boolean initialized;

    public static ISingleSignOn get() {
        ISingleSignOn inst = instance;
        if (inst == null) {
            synchronized (SingleSignOn.class) {
                inst = instance;
                if (inst == null) {
                    instance = inst = YMP.get().getModuleManager().getModule(SingleSignOn.class);
                }
            }
        }
        return inst;
    }

    public SingleSignOn() {
    }

    public SingleSignOn(ISingleSignOnConfig config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize(IApplication owner) throws Exception {
        if (!initialized) {
            //
            YMP.showVersion("Initializing ymate-module-sso-${version}", new Version(2, 0, 0, SingleSignOn.class, Version.VersionType.Release));
            //
            this.owner = owner;
            if (config == null) {
                IApplicationConfigureFactory configureFactory = owner.getConfigureFactory();
                if (configureFactory != null) {
                    IApplicationConfigurer configurer = configureFactory.getConfigurer();
                    IModuleConfigurer moduleConfigurer = configurer == null ? null : configurer.getModuleConfigurer(MODULE_NAME);
                    if (moduleConfigurer != null) {
                        config = DefaultSingleSignOnConfig.create(configureFactory.getMainClass(), moduleConfigurer);
                    } else {
                        config = DefaultSingleSignOnConfig.create(configureFactory.getMainClass(), DefaultModuleConfigurer.createEmpty(MODULE_NAME));
                    }
                }
                if (config == null) {
                    config = DefaultSingleSignOnConfig.defaultConfig();
                }
            }
            if (!config.isInitialized()) {
                config.initialize(this);
            }
            if (config.isEnabled()) {
                InterceptSettings interceptSettings = owner.getInterceptSettings();
                interceptSettings.registerInterceptAnnotation(UserSessionAlready.class, UserSessionAlreadyInterceptor.class);
                interceptSettings.registerInterceptAnnotation(UserSessionCheck.class, UserSessionCheckInterceptor.class);
                interceptSettings.registerInterceptAnnotation(UserSessionConfirm.class, UserSessionConfirmInterceptor.class);
                interceptSettings.registerInterceptAnnotation(UserSessionStatus.class, UserSessionStatusInterceptor.class);
                //
                IBeanFactory beanFactory = owner.getBeanFactory();
                beanFactory.registerBean(BeanMeta.create(UserSessionAlreadyInterceptor.class, true));
                beanFactory.registerBean(BeanMeta.create(UserSessionCheckInterceptor.class, true));
                beanFactory.registerBean(BeanMeta.create(UserSessionConfirmInterceptor.class, true));
                beanFactory.registerBean(BeanMeta.create(UserSessionStatusInterceptor.class, true));
                //
                IWebMvc mvc = owner.getModuleManager().getModule(WebMVC.class);
                if (config.isGeneralAuthEnabled()) {
                    mvc.registerController(config.getServicePrefix(), GeneralAuthController.class);
                }
                if (!config.isClientMode()) {
                    mvc.registerController(config.getServicePrefix(), ServerAuthController.class);
                }
                //
                owner.getEvents().registerListener(Events.MODE.NORMAL, WebEvent.class, (IEventListener<WebEvent>) context -> {
                    if (WebEvent.EVENT.SESSION_DESTROYED.equals(context.getEventName())) {
                        HttpSessionEvent eventSource = context.getEventSource();
                        IToken token = (IToken) eventSource.getSession().getAttribute(IToken.class.getName());
                        if (token != null) {
                            try {
                                ITokenStorageAdapter storageAdapter = config.getTokenStorageAdapter();
                                if (storageAdapter != null) {
                                    storageAdapter.remove(token);
                                    storageAdapter.cleanup(token.getUid());
                                }
                            } catch (Exception e) {
                                if (LOG.isWarnEnabled()) {
                                    LOG.warn(String.format("An exception occurred while cleaning token for user '%s'", token.getUid()), RuntimeUtils.unwrapThrow(e));
                                }
                            }
                        }
                    }
                    return false;
                });
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            initialized = false;
            //
            if (config.isEnabled()) {
                config.getTokenAdapter().close();
                if (!config.isClientMode() && config.getTokenStorageAdapter() != null) {
                    config.getTokenStorageAdapter().close();
                }
            }
            //
            config = null;
            owner = null;
        }
    }

    @Override
    public IApplication getOwner() {
        return owner;
    }

    @Override
    public ISingleSignOnConfig getConfig() {
        return config;
    }

    private IToken checkToken(IToken token) throws Exception {
        if (token != null) {
            boolean needCleanup = isTimeout(token) || isValidationRequired(token) && !config.getTokenAdapter().validateToken(token);
            if (needCleanup) {
                cleanAndRemoveToken(token);
                return null;
            }
        }
        return token;
    }

    @Override
    public IToken getCurrentToken() throws Exception {
        IToken token = (IToken) InterceptContext.getLocalAttributes().get(IToken.class.getName());
        if (token != null) {
            token = checkToken(token);
            if (token == null) {
                InterceptContext.getLocalAttributes().remove(IToken.class.getName());
            }
        }
        if (token == null) {
            token = checkToken(config.getTokenAdapter().getToken());
            if (token != null) {
                InterceptContext.getLocalAttributes().put(IToken.class.getName(), token);
            }
        }
        return token;
    }

    @Override
    public IToken getToken(String tokenId) throws Exception {
        if (config.isClientMode()) {
            throw new UnsupportedOperationException("This operation is not supported in client mode!");
        }
        return checkToken(config.getTokenStorageAdapter().load(tokenId));
    }

    @Override
    public boolean isTimeout(IToken token) {
        long now = System.currentTimeMillis();
        int maxAge = config.getTokenMaxAge();
        return (token.getExpirationTime() > 0 && now > token.getExpirationTime()) || (maxAge > 0 && now - token.getCreateTime() > maxAge * DateTimeUtils.SECOND);
    }

    @Override
    public boolean isValidationRequired(IToken token) {
        int timeInterval = config.getTokenValidationTimeInterval();
        return timeInterval <= 0 || System.currentTimeMillis() - token.getLastValidationTime() > timeInterval * DateTimeUtils.SECOND;
    }

    @Override
    public IToken createToken(String uid, String remoteAddr, String userAgent) throws Exception {
        if (StringUtils.isBlank(uid)) {
            throw new NullArgumentException("uid");
        }
        if (StringUtils.isBlank(remoteAddr)) {
            throw new NullArgumentException("remoteAddr");
        }
        if (StringUtils.isBlank(userAgent)) {
            throw new NullArgumentException("userAgent");
        }
        return ClassUtils.loadClass(ITokenBuilder.class, DefaultTokenBuilder.class)
                .id(config.getTokenAdapter().generateTokenKey())
                .uid(uid)
                .remoteAddr(remoteAddr)
                .userAgent(userAgent)
                .build();
    }

    @Override
    public String saveOrUpdateToken(IToken token) throws Exception {
        return saveOrUpdateToken(token, true);
    }

    @Override
    public String saveOrUpdateToken(IToken token, boolean cookie) throws Exception {
        config.getTokenStorageAdapter().saveOrUpdate(token);
        if (cookie) {
            return config.getTokenAdapter().setToken(token);
        }
        return config.getTokenAdapter().encryptToken(token);
    }

    @Override
    public void cleanAndRemoveToken(IToken token) throws Exception {
        cleanAndRemoveToken(token, true);
    }

    @Override
    public void cleanAndRemoveToken(IToken token, boolean cookie) throws Exception {
        if (cookie) {
            config.getTokenAdapter().cleanToken();
        }
        config.getTokenStorageAdapter().remove(token);
    }
}
