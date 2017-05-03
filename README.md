### SSO (Single Sign-On)

基于YMP框架实现的单点登录服务模块；

#### Maven包依赖

    <dependency>
        <groupId>net.ymate.module</groupId>
        <artifactId>ymate-module-sso</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

### 模块配置参数说明

    #-------------------------------------
    # module.sso 模块初始化参数
    #-------------------------------------
    
    # 在Cookie中存储令牌的名称, 默认值: module.sso_token
    ymp.configs.module.sso.token_cookie_name=
    
    # 令牌存储在请求头中的名称, 默认值: X-ModuleSSO-Token
    ymp.configs.module.sso.token_header_name=
    
    # 令牌生命周期(秒), 默认值为0, 小于等于0表示不启用
    ymp.configs.module.sso.token_maxage=
    
    # 开启多会话模式(即同一账号允许多处登录), 默认值: false
    ymp.configs.module.sso.multi_session_enabled=
    
    # 开启会话的IP地址检查, 默认值: false
    ymp.configs.module.sso.ip_check_enabled=
    
    # 是否为客户端模式, 默认为false
    ymp.configs.module.sso.client_mode=
    
    # 指定服务端基准URL路径(若客户端模式开启时则此项必填), 必须以'http://'或'https://'开始并以'/'结束, 如: http://www.ymate.net/service/, 默认值: 空
    ymp.configs.module.sso.service_base_url=
    
    # 客户端与服务端之间通讯请求参数签名密钥, 默认值: ""
    ymp.configs.module.sso.service_auth_key=
    
    # 令牌分析适配器接口实现, 默认值: net.ymate.module.sso.impl.DefaultSSOTokenAdapter
    ymp.configs.module.sso.token_adapter_class=
    
    # 令牌存储适配器接口实现, 必选参数, 默认值: 空
    ymp.configs.module.sso.storage_adapter_class=
    
    # 与UserSessionBean整合时, 必须设置此参数
    ymp.params.webmvc.user_session_handler_class=net.ymate.module.sso.support.SSOUserSessionHandler

#### One More Thing

YMP不仅提供便捷的Web及其它Java项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入 官方QQ群480374360，一起交流学习，帮助YMP成长！

了解更多有关YMP框架的内容，请访问官网：http://www.ymate.net/