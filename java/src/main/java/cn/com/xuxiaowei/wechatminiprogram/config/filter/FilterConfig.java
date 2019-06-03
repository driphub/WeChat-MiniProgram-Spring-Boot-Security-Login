package cn.com.xuxiaowei.wechatminiprogram.config.filter;

import cn.com.xuxiaowei.wechatminiprogram.authentication.XcxLoginAuthenticationManager;
import cn.com.xuxiaowei.wechatminiprogram.filter.XcxLoginAbstractAuthenticationProcessingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter 配置
 *
 * @author xuxiaowei
 */
@Configuration
public class FilterConfig {

    /**
     * 小程序 登录 认证处理过滤器 注册为 Bean
     *
     * @return 可使用 Autowired 的 小程序 登录 认证处理过滤器
     */
    @Bean
    XcxLoginAbstractAuthenticationProcessingFilter xcxLoginAbstractAuthenticationProcessingFilterBean() {
        XcxLoginAbstractAuthenticationProcessingFilter xcxLoginFilter = new XcxLoginAbstractAuthenticationProcessingFilter("/xcx/login.do");
        xcxLoginFilter.setAuthenticationManager(new XcxLoginAuthenticationManager());
        return xcxLoginFilter;
    }

    /**
     * 小程序 登录 认证处理过滤器
     */
    @Bean
    public FilterRegistrationBean smsAbstractAuthenticationProcessingFilter() {
        FilterRegistrationBean<XcxLoginAbstractAuthenticationProcessingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(xcxLoginAbstractAuthenticationProcessingFilterBean());
        return registration;
    }

}
