package com.example.studyprojectbackend.config;

import com.alibaba.fastjson.JSONObject;
import com.example.studyprojectbackend.entity.RestBean;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

@Configuration // 标记此类为Spring的配置类
@EnableWebSecurity // 启用Spring Security的Web安全支持
public class SecurityConfiguration {

    @Resource // 注入AuthorizeService，用于用户验证逻辑
    AuthorizeService authorizeService;

    @Resource // 注入DataSource，用于持久化Token
    DataSource dataSource;

    /**
     * 配置安全过滤链，包括请求授权、登录、登出、跨域和异常处理
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository tokenRepository) throws Exception {
        return http
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 配置白名单，允许所有验证相关请求
                        .requestMatchers("/api/auth/**").permitAll()
                        // 所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 配置表单登录
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login") // 登录处理的URL
                        .successHandler(customSuccessHandle()) // 自定义登录成功处理器
                        .failureHandler(customFailureHandle()) // 自定义登录失败处理器
                )
                // 配置登出功能
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // 登出请求的URL
                        .logoutSuccessHandler(customLogoutSuccessHandle()) // 自定义登出成功处理器
                )
                // 配置用户详情服务
                .userDetailsService(authorizeService)

                // 配置"记住我"功能
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("remember") // "记住我"参数名称
                        .tokenRepository(tokenRepository) // Token持久化策略
                        .tokenValiditySeconds(3600 * 24 * 7) // Token有效期为7天
                )

                // 禁用CSRF保护，并配置CORS
                .csrf(csrf -> csrf.disable()) // 禁用CSRF保护

                .cors(cors -> cors.configurationSource(configurationSource())) // 配置跨域

                // 配置自定义未授权处理逻辑
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> { // 认证失败处理器
                            response.setCharacterEncoding("utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回401状态码
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, authException.getMessage()))); // 返回错误信息
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> { // 访问拒绝处理器
                            response.setCharacterEncoding("utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回401状态码
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, accessDeniedException.getMessage()))); // 返回错误信息
                        })
                )
                .build(); // 构建安全过滤链
    }

    /**
     * 配置Token持久化存储策略
     */
    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); // 设置数据源
        jdbcTokenRepository.setCreateTableOnStartup(false); // 是否在启动时创建Token表，设为false
        return jdbcTokenRepository;
    }

    /**
     * 配置跨域策略
     */
    private CorsConfigurationSource configurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*"); // 允许所有来源
        cors.setAllowCredentials(true); // 允许发送凭证
        cors.addAllowedHeader("*"); // 允许所有请求头
        cors.addAllowedMethod("*"); // 允许所有请求方法
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors); // 注册全局跨域配置
        return source;
    }

    /**
     * 配置密码编码器，使用BCrypt算法
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义登录失败处理器
     */
    private AuthenticationFailureHandler customFailureHandle() {
        return (request, response, exception) -> {
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态为200，以便前端处理
            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage()))); // 返回失败信息
        };
    }

    /**
     * 自定义登录成功处理器
     */
    private AuthenticationSuccessHandler customSuccessHandle() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态为200
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功"))); // 返回成功信息
        };
    }

    /**
     * 自定义登出成功处理器
     */
    private LogoutSuccessHandler customLogoutSuccessHandle() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态为200
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登出成功"))); // 返回登出成功信息
        };
    }
}
