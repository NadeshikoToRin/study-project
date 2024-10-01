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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

@Configuration // 将此类标识为Spring的配置类，Spring将其自动扫描和初始化
@EnableWebSecurity // 启用Spring Security的Web安全功能，确保应用程序受保护
public class SecurityConfiguration {

    @Resource // 注入AuthorizeService，用于处理自定义的用户认证逻辑
    AuthorizeService authorizeService;

    @Resource // 注入DataSource，管理数据库连接，主要用于“记住我”Token的持久化
    DataSource dataSource;

    /**
     * 配置安全过滤链，定义安全策略，包括身份验证、授权、跨域处理等
     * @param http HttpSecurity对象，用于配置安全策略
     * @param tokenRepository 持久化Token存储库，用于"记住我"功能
     * @return SecurityFilterChain 安全过滤链，配置了不同的安全组件
     * @throws Exception 抛出异常以防配置出错
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository tokenRepository) throws Exception {
        return http
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 放行所有与身份验证相关的API请求
                        .anyRequest().authenticated() // 其他请求必须经过认证
                )
                // 配置表单登录功能
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login") // 配置处理登录请求的URL
                        .successHandler(customSuccessHandle()) // 配置自定义的登录成功处理器
                        .failureHandler(customFailureHandle()) // 配置自定义的登录失败处理器
                )
                // 配置登出功能
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // 配置登出请求的URL
                        .logoutSuccessHandler(customLogoutSuccessHandle()) // 配置自定义的登出成功处理器
                )
                // 配置用户详情服务，从AuthorizeService中加载用户数据
                .userDetailsService(authorizeService)

                // 配置"记住我"功能
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("remember") // 设置"记住我"功能的请求参数名称
                        .tokenRepository(tokenRepository) // 指定Token持久化策略
                        .tokenValiditySeconds(3600 * 24 * 7) // 设置Token有效期为7天
                )

                // 禁用CSRF保护（适用于非浏览器客户端的API请求）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置跨域处理，允许来自不同域的请求
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 配置异常处理器，处理未授权和访问被拒绝的情况
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> { // 处理认证失败的请求
                            response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回HTTP 401未授权状态
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, authException.getMessage()))); // 返回JSON格式的错误信息
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> { // 处理访问被拒绝的请求
                            response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回HTTP 401未授权状态
                            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, accessDeniedException.getMessage()))); // 返回JSON格式的错误信息
                        })
                )
                .build(); // 构建并返回安全过滤链
    }

    /**
     * 配置Token持久化存储策略，用于实现"记住我"功能
     * @return PersistentTokenRepository 实现Token持久化的存储库
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); // 设置数据源，用于存储Token
        jdbcTokenRepository.setCreateTableOnStartup(false); // 禁止启动时自动创建Token存储表
        return jdbcTokenRepository; // 返回持久化Token存储库
    }

    /**
     * 配置跨域策略，允许应用程序接受来自其他域的请求
     * @return CorsConfigurationSource 定义跨域配置的源
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*"); // 允许所有域请求
        cors.setAllowCredentials(true); // 允许发送凭证（如Cookies）
        cors.addAllowedHeader("*"); // 允许所有请求头
        cors.addAllowedMethod("*"); // 允许所有HTTP方法
        cors.addExposedHeader("*"); // 允许客户端访问所有响应头
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors); // 将跨域配置应用到所有路径
        return source; // 返回跨域配置源
    }

    /**
     * 配置密码编码器，使用BCrypt算法对用户密码进行加密
     * @return BCryptPasswordEncoder 返回BCrypt密码编码器实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用BCrypt对密码进行加密
    }

    /**
     * 自定义登录失败处理器
     * @return AuthenticationFailureHandler 返回登录失败处理器
     */
    private AuthenticationFailureHandler customFailureHandle() {
        return (request, response, exception) -> {
            response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态码为200，便于前端处理
            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage()))); // 返回失败信息
        };
    }

    /**
     * 自定义登录成功处理器
     * @return AuthenticationSuccessHandler 返回登录成功处理器
     */
    private AuthenticationSuccessHandler customSuccessHandle() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态码为200
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功"))); // 返回登录成功的JSON响应
        };
    }

    /**
     * 自定义登出成功处理器
     * @return LogoutSuccessHandler 返回登出成功处理器
     */
    private LogoutSuccessHandler customLogoutSuccessHandle() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
            response.setStatus(HttpServletResponse.SC_OK); // 设置状态码为200
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登出成功"))); // 返回登出成功的JSON响应
        };
    }
}
