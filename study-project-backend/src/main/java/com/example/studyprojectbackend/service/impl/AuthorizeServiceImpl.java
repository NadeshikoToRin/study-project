package com.example.studyprojectbackend.service.impl;

import com.example.studyprojectbackend.entity.auth.Account;
import com.example.studyprojectbackend.mapper.UserMapper;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    //redis操作模板
    @Resource
    StringRedisTemplate template;

    //密码加密
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();




    @Override
    public String sendValidateEmail(String email, String sessionId,boolean requireAccount) {

        String key = "email:" + sessionId + ":" + email+":"+requireAccount;

        //判断redis中是否存在该键值对
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120)
                return "请求频繁，请稍后再试！";
        }

        Account account = userMapper.findAccountByNameOrEmail(email);

        //处理忘记密码请求
        if (requireAccount && account == null) {
            return "没有该账户，请先注册！";
        }
        //处理注册请求
        if (account != null && !requireAccount) {
            return "该邮箱已被注册！";
        }
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
        message.setText("验证码是：" + code+"。\n请勿泄露给其他人，3分钟有效");

        try {
            // 发送邮件
            mailSender.send(message);

            template.opsForValue().set(key, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        } catch (MailException e) {
            e.printStackTrace(); // 捕获异常
            return "发送失败，请检查邮件地址是否有效";
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

    public boolean isExist(String text){
        return userMapper.findAccountByNameOrEmail(text) != null;
    }


    // 校验验证码和注册
    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email:" + sessionId + ":" + email+":"+false;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            if (s == null) return "验证码失效，请重新请求";
            if (s.equals(code)){
                Account account = userMapper.findAccountByNameOrEmail(username);
                if (account != null) return "该用户名已被注册";
                //删除redis中的数据
                template.delete(key);
                //加密密码
                password = encoder.encode(password);
                if (userMapper.createAccount(username,email,password) > 0) {
                    return null;
                } else {
                    return "内部错误，请联系管理员";
                }
            }else {
                return "验证码错误";
            }
        }else {
            return "请先获取验证码";
        }
    }
    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":" + true;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            if (s == null) return "验证码失效，请重新请求";
            if (s.equals(code)){
                //删除redis中的数据
                template.delete(key);
                return null;
            }else {
                return "验证码错误";
            }
        }else {
            return "请先获取验证码";
        }
    }

    @Override
    public boolean resetPassword(String password, String email) {
        //加密密码
        password = encoder.encode(password);
        //更新密码
        return userMapper.resetPasswordByEmail(password, email) >0;
    }
}
