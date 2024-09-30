package com.example.studyprojectbackend.interceptor;

import com.example.studyprojectbackend.entity.user.AccountUser;
import com.example.studyprojectbackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前用户
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取认证信息
        Authentication authentication = context.getAuthentication();
        // 打印认证信息
        User user  = (User)authentication.getPrincipal();

        String username = user.getUsername();

        AccountUser account = userMapper.findAccountUserByNameOrEmail(username);

        request.getSession().setAttribute("account", account);

        return true;
    }
}
