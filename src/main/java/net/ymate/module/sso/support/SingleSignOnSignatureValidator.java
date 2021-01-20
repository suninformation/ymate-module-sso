 /*
  * Copyright 2007-2021 the original author or authors.
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
 package net.ymate.module.sso.support;

 import net.ymate.module.sso.SingleSignOn;
 import net.ymate.platform.commons.util.ParamUtils;
 import net.ymate.platform.webmvc.IWebMvc;
 import net.ymate.platform.webmvc.RequestMeta;
 import net.ymate.platform.webmvc.annotation.SignatureValidate;
 import net.ymate.platform.webmvc.impl.DefaultSignatureValidator;
 import org.apache.commons.lang3.StringUtils;

 import java.util.Map;

 /**
  * @author 刘镇 (suninformation@163.com) on 2021/01/20 21:10
  * @since 2.0.0
  */
 public class SingleSignOnSignatureValidator extends DefaultSignatureValidator {

     @Override
     public boolean validate(IWebMvc owner, RequestMeta requestMeta, SignatureValidate signatureValidate) {
         SingleSignOn singleSignOn = owner.getOwner().getModuleManager().getModule(SingleSignOn.class);
         return singleSignOn == null || !StringUtils.isNotBlank(singleSignOn.getConfig().getServiceAuthKey()) || super.validate(owner, requestMeta, signatureValidate);
     }

     @Override
     protected String doSignature(IWebMvc owner, SignatureValidate signatureValidate, Map<String, Object> signatureParams) {
         String proxyServiceAuthKey = owner.getOwner().getModuleManager().getModule(SingleSignOn.class).getConfig().getServiceAuthKey();
         return ParamUtils.createSignature(signatureParams, signatureValidate.encode(), signatureValidate.upperCase(), proxyServiceAuthKey);
     }
 }
