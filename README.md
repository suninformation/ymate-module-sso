# YMATE-MODULE-SSO

[![Maven Central status](https://img.shields.io/maven-central/v/net.ymate.module/ymate-module-unpack.svg)](https://search.maven.org/artifact/net.ymate.module/ymate-module-unpack)
[![LICENSE](https://img.shields.io/github/license/suninformation/ymate-module-unpack.svg)](https://gitee.com/suninformation/ymate-module-unpack/blob/master/LICENSE)

基于 YMP 框架实现的单点登录模块封装。



## Maven包依赖

```xml
<dependency>
    <groupId>net.ymate.module</groupId>
    <artifactId>ymate-module-unpack</artifactId>
    <version>1.0.0</version>
</dependency>
```



## 模块配置参数说明

```properties
#-------------------------------------
# module.sso 模块初始化参数
#-------------------------------------

# 在Cookie中存储令牌的名称, 默认值: module.sso_token
ymp.configs.module.sso.token_cookie_name=

# 令牌存储在请求头中的名称, 默认值: X-ModuleSSO-Token
ymp.configs.module.sso.token_header_name=

# 令牌URL参数名称, 默认值: token
ymp.configs.module.sso.token_param_name=

# 令牌生命周期(秒), 默认值为0, 小于等于0表示不启用
ymp.configs.module.sso.token_max_age=

# 令牌有效性验证的时间间隔(秒), 默认值: 0
ymp.configs.module.sso.token_validation_time_interval=

# 缓存名称前缀, 默认值: ""
ymp.configs.module.sso.cache_name_prefix=

# 开启多会话模式(即同一账号允许多处登录), 默认值: false
ymp.configs.module.sso.multi_session_enabled=

# 同一账号最多会话数量，小于等于0表示不限制
ymp.configs.module.sso.multi_session_max_count=

# 开启会话的IP地址检查, 默认值: false
ymp.configs.module.sso.ip_check_enabled=

# 是否为客户端模式, 默认为false
ymp.configs.module.sso.client_mode=

# 指定服务端基准URL路径(若客户端模式开启时则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/service/, 默认值: 空
ymp.configs.module.sso.service_base_url=

# 客户端与服务端之间通讯请求参数签名以及令牌加解密附加密钥, 默认值: ""
ymp.configs.module.sso.service_auth_key=

# 服务请求映射前缀(不允许'/'开始和结束), 默认值: ""
ymp.configs.module.sso.service_prefix=

# 是否注册通用令牌验证控制器, 默认值: false
ymp.configs.module.sso.general_auth_enabled=

# 令牌分析适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenAdapter
ymp.configs.module.sso.token_adapter_class=

# 令牌存储适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenStorageAdapter
ymp.configs.module.sso.token_storage_adapter_class=

# 令牌自定义属性加载适配器接口实现, 非客户端模式时有效, 默认值: 空
ymp.configs.module.sso.token_attribute_adapter_class=

# 检测令牌无效时重定向URL地址(用于跳转至用户登录页面, 如: login?redirect_url=${redirect_url}), 默认值: 空
ymp.configs.module.sso.token_invalid_redirect_url=

# 检测令牌已存在时重定向URL地址(用于登录成功后跳转), 默认值: 空
ymp.configs.module.sso.token_already_redirect_url=

# 是否开启会话安全确认, 默认值: false
ymp.configs.module.sso.token_confirm_enabled=

# 会话安全确认处理器接口实现, 默认值: net.ymate.module.sso.impl.DefaultTokenConfirmHandler
ymp.configs.module.sso.token_confirm_handler_class=

# 会话安全确认重定向URL地址, 默认值: confirm?redirect_url=${redirect_url}
ymp.configs.module.sso.token_confirm_redirect_url=

# 会话安全确认超时时间(分钟)，小于等于0则使用默认值: 30
ymp.configs.module.sso.token_confirm_timeout=
```



## 示例代码

配置用户会话检查拦截器，该拦截器已集成单点登录相关处理逻辑：

```java
@RequestMapping(value = "/user/profile/edit", method = Type.HttpMethod.POST)
@UserSessionCheck
public IView doEditUserProfile(@RequestParam String nickName, ......) throws Exception {
    // ...... 省略
    return WebResult.success().toJsonView();
}
```

通过代码操作用户的登录授权令牌对象：

```java
ISingleSignOn singleSignOn = SingleSignOn.get();
IToken token = singleSignOn.getCurrentToken();
if (token == null) {
    // 若返回值为空则表示令牌对象不存在
    token = singleSignOn.createToken("uid_xxx", "127.0.0.1", "FireFox");
    // 存储令牌并使其生效
    singleSignOn.saveOrUpdateToken(token);
}
if (token != null) {
    // 令牌唯一标识
    token.getId();
    // 用户唯一标识
    token.getUid();
    // 用户IP地址
    token.getRemoteAddr();
    // 用户代理信息
    token.getUserAgent();
    // 令牌对象创建时间
    token.getCreateTime();
    // 令牌扩展属性
    token.getAttributes();
}
```



## One More Thing

YMP 不仅提供便捷的 Web 及其它 Java 项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入官方 QQ 群：[480374360](https://qm.qq.com/cgi-bin/qm/qr?k=3KSXbRoridGeFxTVA8HZzyhwU_btZQJ2)，一起交流学习，帮助 YMP 成长！

如果喜欢 YMP，希望得到你的支持和鼓励！

![Donation Code](https://ymate.net/img/donation_code.png)

了解更多有关 YMP 框架的内容，请访问官网：[https://ymate.net](https://ymate.net)