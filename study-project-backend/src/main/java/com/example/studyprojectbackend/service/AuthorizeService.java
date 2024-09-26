package com.example.studyprojectbackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 发送验证邮件
    boolean sendValidateEmail(String email,String sessionId);

    boolean isExist(String text);


}
