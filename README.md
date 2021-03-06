# 捐助与转载

- 整理不易，捐助随意。

- 尊重他人的劳动成果，转载请注明地址。

<p align=center>
  <a href="https://xuxiaowei.com.cn">
    <img src="https://cdn2.xuxiaowei.com.cn/img/QRCode.png/xuxiaowei.com.cn" alt="徐晓伟工作室" width="360">
  </a>
</p>


# WeChat-MiniProgram-Spring-Boot-Security-Login
- 微信小程序集成 Spring Boot Security 登录鉴权。
- 微信小程序集成 weapp-cookie，一行代码为你的小程序实现 Cookie 机制。[weapp-cookie](https://github.com/charleslo1/weapp-cookie)
- 微信小程序集成 weixin-java-miniapp 微信开发 Java SDK。[WxJava](https://github.com/Wechat-Group/WxJava)

# 依赖

## Spring Boot 依赖（创建项目时选择）

- Spring Boot
    - 2.1.5.RELEASE

- Core
    - Lombok                    注解（Getter/Setter）。
    - Configuration Processor   为您的自定义配置键生成元数据（注解处理器）。
	
- Web
    - Web                       使用Tomcat和Spring MVC进行全栈Web开发
    
- Security
    - Security                  通过spring-security保护您的应用程序
    
    
## 其他依赖（创建项目时不可选）

- weixin-java-miniapp           微信小程序 Java SDK。[WxJava](https://github.com/Wechat-Group/WxJava)

- Fastjson                      阿里巴巴 JSON（非必须）

- weapp-cookie					小程序原生的 request 网络请求接口并不支持传统的 Cookie，
								但有时候我们现有的后端接口确于依赖 Cookie（比如服务器用户登录态），
								这个库可用一行代码为你的小程序实现 Cookie 机制，以保证基于 cookie 的服务会话不会失效，与 web 端共用会话机制。
								[weapp-cookie](https://github.com/charleslo1/weapp-cookie)
