package com.example.studyprojectbackend.config;

import com.example.studyprojectbackend.interceptor.AuthorizeInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 表示该类是Spring的配置类
public class WebConfiguration implements WebMvcConfigurer {

    @Resource // 注入AuthorizeInterceptor，用于拦截器逻辑
    AuthorizeInterceptor interceptor;

    /**
     * 配置拦截器规则，定义哪些请求路径需要拦截
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将AuthorizeInterceptor注册到拦截器链，拦截所有请求
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/api/auth/**"); // 排除身份验证相关的路径，放行这些请求
    }
}
