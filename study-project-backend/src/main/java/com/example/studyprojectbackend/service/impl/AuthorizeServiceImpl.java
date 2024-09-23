package com.example.studyprojectbackend.service.impl;

import com.example.studyprojectbackend.entity.Account;
import com.example.studyprojectbackend.mapper.UserMapper;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

//权限校验service
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    /**
     * 1.先生成对应验证码
     * 2.把邮箱和验证码放入Redis中，设置过期时间为3分钟，
     *  剩余时间小于2分钟可以重新发送一次
     * 3.发送验证码到指定邮箱
     * 4.如果发送失败删除redis中插入的数据
     * 5.用户注册时，再从redis里取出键值对，判断是否一致
     */

    //邮箱发送器
    @Resource
    MailSender mailSender;

    //用户数据库
    @Resource
    UserMapper userMapper;

    @Value("${spring.mail.username}")

    String from;


    @Override
    public boolean sendValidateEmail(String email) {
        // 生成验证码
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;

        // 创建邮件信息
        SimpleMailMessage message = new SimpleMailMessage();
        // 发送者
        message.setFrom(from);
        // 收件者
        message.setTo(email); // 需要设置收件人
        // 主题
        message.setSubject("您的验证邮箱");
        // 邮件内容
        message.setText("验证码是：" + code);

        try {
            // 发送邮件
            mailSender.send(message);
            return true;
        } catch (MailException e) {
            e.printStackTrace(); // 捕获异常
            return false;
        }
    }




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
