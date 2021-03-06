package cn.com.xuxiaowei.wechatminiprogram.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security 安全控制 配置
 *
 * @author xuxiaowei
 */
@Configuration
public class WebSecurityConfigurerAdapterConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 这里重写，禁用了账户名密码登录
        // <code>super.configure(http);</code>

        // 用户详细信息操作，需要授权通过
        // 此时不能包含：ROLE_
        http.authorizeRequests().antMatchers("/userInfo/**").hasRole("WECHATXCX");

        // 提交 POST
        http.csrf().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

}
