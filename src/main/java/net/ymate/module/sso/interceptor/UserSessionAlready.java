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

import net.ymate.platform.core.beans.annotation.InterceptAnnotation;
import net.ymate.platform.core.beans.intercept.IInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/02/16 16:48
 * @since 2.0.0
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InterceptAnnotation(IInterceptor.Direction.BEFORE)
public @interface UserSessionAlready {

    /**
     * @return 保持沉默（表示不转到消息提示视图而是直接跳转）
     */
    boolean observeSilence() default false;

    /**
     * @return URL地址重定向时间间隔
     */
    int timeInterval() default 0;

    /**
     * @return 自定义重定向URL地址
     */
    String redirectUrl() default StringUtils.EMPTY;
}
