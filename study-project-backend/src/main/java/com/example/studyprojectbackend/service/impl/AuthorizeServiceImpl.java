package com.example.studyprojectbackend.service.impl;

import com.example.studyprojectbackend.entity.Account;
import com.example.studyprojectbackend.mapper.UserMapper;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//权限校验service
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Override
    public boolean sendValidateEmail(String email) {

        /**
         * 1.先生成对应验证码
         * 2.把邮箱和验证码放入Redis中，设置过期时间为3分钟，
         *  剩余时间小于2分钟可以重新发送一次
         * 3.发送验证码到指定邮箱
         * 4.如果发送失败删除redis中插入的数据
         * 5.用户注册时，再从redis里取出键值对，判断是否一致
         */
        return false;
    }

    @Resource
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //判断用户名是否为空
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account accountByNameOrEmail = userMapper.findAccountByNameOrEmail(username);
        //判断用户是否存在
        if (accountByNameOrEmail == null) {
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
