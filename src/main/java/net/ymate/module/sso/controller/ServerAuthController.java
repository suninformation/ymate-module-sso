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
package net.ymate.module.sso.controller;

import net.ymate.module.sso.ISingleSignOnConfig;
import net.ymate.module.sso.IToken;
import net.ymate.module.sso.ITokenAttributeAdapter;
import net.ymate.module.sso.SingleSignOn;
import net.ymate.module.sso.impl.DefaultSignatureExtraParamProcessor;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.validation.validate.VRequired;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.annotation.SignatureValidate;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.util.WebErrorCode;
import net.ymate.platform.webmvc.util.WebResult;
import net.ymate.platform.webmvc.view.IView;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/05/04 01:29
 */
public class ServerAuthController {

    @Inject
    private SingleSignOn owner;

    /**
     * @param tokenId    令牌唯一标识
     * @param uid        用户唯一标识
     * @param userAgent  用户代理
     * @param remoteAddr 用户IP地址
     * @return 验证客户端令牌有效性及状态
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = ISingleSignOnConfig.DEFAULT_CONTROLLER_MAPPING, method = Type.HttpMethod.POST)
    @SignatureValidate(processorClass = DefaultSignatureExtraParamProcessor.class)
    public IView authorize(@VRequired @RequestParam(IToken.PARAM_TOKEN_ID) String tokenId,
                           @VRequired @RequestParam String uid,
                           @VRequired @RequestParam(IToken.PARAM_USER_AGENT) String userAgent,
                           @VRequired @RequestParam(IToken.PARAM_REMOTE_ADDR) String remoteAddr) throws Exception {
        // 尝试从存储中加载原始令牌数据并进行有效性验证
        IToken token = owner.getToken(tokenId);
        if (token != null && StringUtils.equals(token.getUid(), uid) && StringUtils.equals(token.getUserAgent(), userAgent)) {
            boolean ipCheckFailed = owner.getConfig().isIpCheckEnabled() && !StringUtils.equals(remoteAddr, token.getRemoteAddr());
            if (!ipCheckFailed) {
                // 尝试加载令牌自定义属性
                ITokenAttributeAdapter attributeAdapter = owner.getConfig().getTokenAttributeAdapter();
                if (attributeAdapter != null) {
                    attributeAdapter.loadAttributes(token);
                }
                return WebResult.succeed().data(token.getAttributes()).toJsonView();
            }
        }
        return WebResult.create(WebErrorCode.userSessionInvalidOrTimeout()).toJsonView();
    }
}
