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

import net.ymate.module.sso.SingleSignOn;
import net.ymate.platform.webmvc.ISignatureExtraParamProcessor;
import net.ymate.platform.webmvc.IWebMvc;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/2/18 4:35 下午
 * @since 2.0.0
 */
public class DefaultSignatureExtraParamProcessor implements ISignatureExtraParamProcessor {

    @Override
    public String[] getExtraParams(IWebMvc owner, Map<String, Object> signatureParams) {
        SingleSignOn singleSignOn = owner.getOwner().getModuleManager().getModule(SingleSignOn.class);
        if (StringUtils.isNotBlank(singleSignOn.getConfig().getServiceAuthKey())) {
            return new String[]{singleSignOn.getConfig().getServiceAuthKey()};
        }
        return null;
    }
}
