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
package net.ymate.module.sso.controller;

import net.ymate.framework.commons.ParamUtils;
import net.ymate.framework.webmvc.ErrorCode;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.sso.ISSOToken;
import net.ymate.module.sso.ISSOTokenStorageAdapter;
import net.ymate.module.sso.SSO;
import net.ymate.platform.webmvc.annotation.Controller;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.impl.HttpStatusView;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/5/4 上午1:29
 * @version 1.0
 */
@Controller
@RequestMapping("/sso")
public class SSOTokenController {

    @RequestMapping(value = "/authorize", method = Type.HttpMethod.POST)
    public IView __doAuthorize(@RequestParam String id,
                               @RequestParam String uid,
                               @RequestParam("remote_addr") String remoteAddr,
                               @RequestParam String sign) throws Exception {

        if (SSO.get().getModuleCfg().isClientMode()) {
            return HttpStatusView.NOT_FOUND;
        }
        //
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(remoteAddr) && StringUtils.isNotBlank(sign)) {
            Map<String, String> _params = new HashMap<String, String>();
            _params.put("id", id);
            _params.put("uid", uid);
            _params.put("remote_addr", remoteAddr);
            //
            String _sign = ParamUtils.createSignature(_params, false, SSO.get().getModuleCfg().getServiceAuthKey());
            if (StringUtils.equals(sign, _sign)) {
                ISSOTokenStorageAdapter _storageAdapter = SSO.get().getModuleCfg().getTokenStorageAdapter();
                // 尝试从存储中加载原始令牌数据并进行有效性验证
                ISSOToken _token = _storageAdapter.load(uid, id);
                if (_token != null) {
                    boolean _ipCheck = (SSO.get().getModuleCfg().isIpCheckEnabled() && !StringUtils.equals(remoteAddr, _token.getRemoteAddr()));
                    if (_token.timeout() || !_token.verified() || _ipCheck) {
                        _storageAdapter.remove(_token.getUid(), _token.getId());
                        return WebResult.CODE(ErrorCode.USER_SESSION_INVALID_OR_TIMEOUT).toJSON();
                    } else {
                        return WebResult.SUCCESS().toJSON();
                    }
                }
            }
        }
        return WebResult.CODE(ErrorCode.INVALID_PARAMS_VALIDATION).toJSON();
    }
}
