package com.example.studyprojectbackend.config;

import com.alibaba.fastjson.JSONObject;
import com.example.studyprojectbackend.entity.RestBean;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 配置请求的授权
                .authorizeHttpRequests(auth -> auth
                        // 所有请求都需要认证后才能访问
                        .anyRequest().authenticated()
                )
                // 配置表单登录
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(customSuccessHandle())
                        .failureHandler(customFailureHandle())
                )
                // 配置登出操作
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandle())
                )
                // 配置用户信息
                .userDetailsService(authorizeService)
                // 禁用 CSRF 保护
                .csrf(csrf -> csrf.disable())
                // 配置自定义的未授权处理
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setCharacterEncoding("utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, authException.getMessage())));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setCharacterEncoding("utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, accessDeniedException.getMessage())));
                        })
                )
                .build();
    }

    // 配置认证管理器
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        return security
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and()
                .build();
    }

    // 配置密码加密
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationFailureHandler customFailureHandle() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
            }
        };
    }

    public AuthenticationSuccessHandler customSuccessHandle() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
            }
        };
    }

    public LogoutSuccessHandler customLogoutSuccessHandle() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JSONObject.toJSONString(RestBean.success("登出成功")));
            }
        };
    }

}


