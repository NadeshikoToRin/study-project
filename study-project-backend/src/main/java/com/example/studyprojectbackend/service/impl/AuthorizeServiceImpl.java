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

/**
 * AuthorizeServiceImpl类实现了AuthorizeService接口，
 * 主要负责处理与用户注册、登录、邮箱验证等相关的服务。
 * 包含了发送验证邮件、验证码校验、用户注册、密码重置等核心功能。
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    /**
     * 注解标明的逻辑步骤：
     * 1.生成验证码
     * 2.将邮箱和验证码存入Redis中，设置过期时间为3分钟
     * 3.发送验证码到指定邮箱，2分钟内不允许再次发送
     * 4.如果邮件发送失败，则删除Redis中的数据
     * 5.用户注册时，从Redis中取出验证码并验证是否一致
     */

    // 邮件发送器，用于发送验证邮件
    @Resource
    MailSender mailSender;

    // 数据库操作的Mapper，用于查询用户数据
    @Resource
    UserMapper userMapper;

    // 从配置文件中读取邮件发送者的地址
    @Value("${spring.mail.username}")
    String from;

    // Redis模板，用于操作Redis数据库，存储验证码等信息
    @Resource
    StringRedisTemplate template;

    // 密码加密器，用于加密用户密码，BCrypt算法是一种常用的加密方式
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    /**
     * 发送验证邮件的方法，根据sessionId和邮箱生成Redis的键，并存储验证码。
     * 同时发送验证码到指定邮箱，确保在Redis中有效期为3分钟。
     *
     * @param email          要发送验证码的邮箱地址
     * @param sessionId      当前会话的Session ID，用于唯一标识用户的会话
     * @param requireAccount 是否要求该邮箱必须已存在用户账户（用于找回密码）
     * @return 返回null表示邮件发送成功，其他字符串表示错误信息
     */
    @Override
    public String sendValidateEmail(String email, String sessionId, boolean requireAccount) {

        // Redis中的键，包含sessionId、邮箱和标识是否需要账户存在
        String key = "email:" + sessionId + ":" + email + ":" + requireAccount;

        // 检查Redis中是否已有该键（验证码是否已发送）
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            // 如果剩余时间大于120秒，拒绝再次发送
            if (expire > 120)
                return "请求频繁，请稍后再试！";
        }

        // 从数据库中查询用户账号信息
        Account account = userMapper.findAccountByNameOrEmail(email);

        // 如果需要账户存在但找不到该账户，返回错误
        if (requireAccount && account == null) {
            return "没有该账户，请先注册！";
        }
        // 如果账户已存在但请求是注册，返回错误
        if (account != null && !requireAccount) {
            return "该邮箱已被注册！";
        }

        // 生成6位数随机验证码
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;

        // 创建并设置邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // 发送者
        message.setTo(email); // 收件者
        message.setSubject("您的验证邮箱"); // 邮件主题
        message.setText("验证码是：" + code + "。\n请勿泄露给其他人，3分钟有效"); // 邮件正文

        try {
            // 发送邮件
            mailSender.send(message);
            // 将验证码存入Redis，有效期3分钟
            template.opsForValue().set(key, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        } catch (MailException e) {
            e.printStackTrace(); // 捕获并打印异常信息
            return "发送失败，请检查邮件地址是否有效";
        }
    }

    /**
     * 根据用户名加载用户信息，供Spring Security进行身份验证。
     *
     * @param username 输入的用户名
     * @return 返回包含用户详细信息的UserDetails对象
     * @throws UsernameNotFoundException 当找不到该用户名时抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 检查用户名是否为空
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        // 通过用户名或邮箱从数据库查找账户信息
        Account accountByNameOrEmail = userMapper.findAccountByNameOrEmail(username);
        // 如果用户不存在，抛出异常
        if (accountByNameOrEmail == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 构建UserDetails对象，供Spring Security使用
        return User
                .withUsername(accountByNameOrEmail.getUsername())
                .password(accountByNameOrEmail.getPassword())
                .authorities("user") // 设置用户权限为"user"
                .build();
    }

    /**
     * 判断用户是否存在
     *
     * @param text 需要验证的用户名或邮箱
     * @return 如果用户存在则返回true，否则返回false
     */
    public boolean isExist(String text) {
        return userMapper.findAccountByNameOrEmail(text) != null;
    }

    /**
     * 校验验证码并完成注册
     *
     * @param username   用户名
     * @param password   用户密码
     * @param email      邮箱
     * @param code       验证码
     * @param sessionId  会话ID
     * @return null表示注册成功，其他字符串表示错误信息
     */
    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        // Redis中的键，用于校验验证码
        String key = "email:" + sessionId + ":" + email + ":" + false;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            // 验证码失效
            if (s == null) return "验证码失效，请重新请求";
            // 验证码匹配成功
            if (s.equals(code)) {
                Account account = userMapper.findAccountByNameOrEmail(username);
                if (account != null) return "该用户名已被注册";
                // 删除Redis中的验证码
                template.delete(key);
                // 对用户密码进行加密
                password = encoder.encode(password);
                // 在数据库中创建新用户
                if (userMapper.createAccount(username, email, password) > 0) {
                    return null; // 注册成功
                } else {
                    return "内部错误，请联系管理员"; // 数据库操作失败
                }
            } else {
                return "验证码错误"; // 验证码不匹配
            }
        } else {
            return "请先获取验证码"; // Redis中无验证码记录
        }
    }

    /**
     * 仅验证验证码是否正确，用于密码重置流程。
     *
     * @param email     用户邮箱
     * @param code      验证码
     * @param sessionId 会话ID
     * @return null表示验证成功，其他字符串表示错误信息
     */
    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":" + true;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            if (s == null) return "验证码失效，请重新请求";
            if (s.equals(code)) {
                template.delete(key); // 验证成功后删除Redis中的验证码
                return null; // 验证通过
            } else {
                return "验证码错误"; // 验证码不匹配
            }
        } else {
            return "请先获取验证码"; // Redis中无验证码记录
        }
    }

    /**
     * 重置用户密码
     *
     * @param password 新密码
     * @param email    用户邮箱
     * @return true表示密码重置成功，false表示失败
     */
    @Override
    public boolean resetPassword(String password, String email) {
        // 加密新密码
        password = encoder.encode(password);
        // 更新数据库中的密码
        return userMapper.resetPasswordByEmail(password, email) > 0;
    }
}
