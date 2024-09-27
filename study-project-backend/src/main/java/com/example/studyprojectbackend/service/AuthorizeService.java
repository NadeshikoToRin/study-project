package com.example.studyprojectbackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 发送验证邮件
    String sendValidateEmail(String email,String sessionId);

    boolean isExist(String text);

    // 验证验证码并注册
    String validateAndRegister(String username,String password,String email,String code,String sessionId);


}
