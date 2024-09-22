package com.example.studyprojectbackend.service;

import com.example.studyprojectbackend.entity.Account;
import com.example.studyprojectbackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//权限校验service
@Service
public class AuthorizeService implements UserDetailsService {

    @Resource
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //判断用户名是否为空
        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account accountByNameOrEmail = userMapper.findAccountByNameOrEmail(username);
        //判断用户是否存在
        if (accountByNameOrEmail == null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //返回用户信息
        return User
                .withUsername(accountByNameOrEmail.getUsername())
                .password(accountByNameOrEmail.getPassword())
                .authorities("user")
                .build();
    }
}
