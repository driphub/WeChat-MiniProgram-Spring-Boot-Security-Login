package cn.com.xuxiaowei.wechatminiprogram.config.handlerinterceptor;

import cn.com.xuxiaowei.wechatminiprogram.handlerinterceptor.XcxLoginSuccessHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器 配置
 *
 * @author xuxiaowei
 */
@Configuration
public class HandlerInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        xcxLoginSuccessHandlerInterceptor(registry);

    }

    /**
     * 小程序登录成功 拦截器
     */
    private void xcxLoginSuccessHandlerInterceptor(InterceptorRegistry registry) {

        // 拦截URL
        List<String> smsLoginAdd = new ArrayList<>();
        // 排除拦截URL
        List<String> smsLoginExclude = new ArrayList<>();

        smsLoginAdd.add("/");

        registry.addInterceptor(new XcxLoginSuccessHandlerInterceptor())
                .addPathPatterns(smsLoginAdd)
                .excludePathPatterns(smsLoginExclude);

    }

}
