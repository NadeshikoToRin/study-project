package com.example.studyprojectbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthorizeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前用户
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取认证信息
        Authentication authentication = context.getAuthentication();
        // 打印认证信息
        System.out.println(authentication.getPrincipal());

        return true;
    }
}
