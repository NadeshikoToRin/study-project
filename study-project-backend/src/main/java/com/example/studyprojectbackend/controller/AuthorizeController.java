package com.example.studyprojectbackend.controller;

import com.example.studyprojectbackend.entity.RestBean;
import com.example.studyprojectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 控制器负责处理登录和注册请求
@Validated // 开启校验功能，确保请求参数符合约束
@RestController // 标记为REST控制器，返回数据而非视图
@RequestMapping("/api/auth") // 定义统一的路由前缀
public class AuthorizeController {

    // 邮箱正则表达式，限制邮箱格式
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    // 用户名称正则表达式，限制用户名只能包含字母、数字、汉字
    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource // 自动注入AuthorizeService，用于处理授权和认证相关的业务逻辑
    AuthorizeService authorizeService;

    /**
     * 用户注册时验证邮箱
     * 通过发送验证码邮件进行验证
     *
     * @param email 用户提交的邮箱
     * @param session 用于获取当前会话的session
     * @return 返回包含结果的RestBean对象
     */
    @RequestMapping("/valid-register-email")
    public RestBean<String> validateRegisterEmail(@Pattern(regexp = EMAIL_REGEX) // 校验邮箱格式
                                                  @RequestParam("email") String email,
                                                  HttpSession session) {
        // 调用业务层方法发送验证码邮件
        String s = authorizeService.sendValidateEmail(email, session.getId(), false);
        if (s == null)
            return RestBean.success("邮件已发送，请注意查收"); // 发送成功，返回成功消息
        else
            return RestBean.failure(400, s); // 发送失败，返回错误信息
    }

    /**
     * 忘记密码时验证邮箱，发送邮件
     *
     * @param email 用户提交的邮箱
     * @param session 用于获取当前会话的session
     * @return 返回包含结果的RestBean对象
     */
    @RequestMapping("/valid-reset-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX) // 校验邮箱格式
                                          @RequestParam("email") String email,
                                          HttpSession session) {
        // 调用业务层方法发送验证码邮件
        String s = authorizeService.sendValidateEmail(email, session.getId(), true);
        if (s == null)
            return RestBean.success("邮件已发送，请注意查收"); // 发送成功，返回成功消息
        else
            return RestBean.failure(400, s); // 发送失败，返回错误信息
    }

    /**
     * 检查用户名是否已经存在
     *
     * @param requestBody 请求体，包含要检查的用户名
     * @return 返回包含结果的RestBean对象
     */
    @RequestMapping(value = "/verify-saved", method = RequestMethod.POST)
    public RestBean<String> verifySaved(@RequestBody Map<String, String> requestBody) {
        String text = requestBody.get("text");
        if (text == null) {
            return RestBean.failure(400, "缺少参数: text"); // 检查参数是否存在
        }
        if (!authorizeService.isExist(text)) {
            return RestBean.success(); // 用户名不存在，返回成功消息
        } else {
            return RestBean.failure(400, "用户已存在"); // 用户名已存在，返回错误信息
        }
    }

    /**
     * 用户注册
     *
     * @param username 用户提交的用户名
     * @param password 用户提交的密码
     * @param email 用户提交的邮箱
     * @param code 用户提交的验证码
     * @param session 获取当前会话session
     * @return 返回包含结果的RestBean对象
     */
    @PostMapping("/register")
    public RestBean<String> register(@Pattern(regexp = USERNAME_REGEX) @RequestParam("username") String username,
                                     @Length(min = 6, max = 16) @RequestParam("password") String password,
                                     @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                     @Length(min = 6, max = 6) @RequestParam("code") String code,
                                     HttpSession session) {
        // 调用业务层方法进行注册验证
        String s = authorizeService.validateAndRegister(username, password, email, code, session.getId());
        if (s == null) {
            return RestBean.success("注册成功!"); // 注册成功，返回成功消息
        } else {
            return RestBean.failure(400, s); // 注册失败，返回错误信息
        }
    }

    /**
     * 忘记密码功能，处理步骤包括：
     * 1. 发送验证邮件
     * 2. 验证sessionId + email + code是否匹配
     * 3. 修改密码
     *
     * @param email 用户提交的邮箱
     * @param code 用户提交的验证码
     * @param session 获取当前会话session
     * @return 返回包含结果的RestBean对象
     */
    @RequestMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX)
                                       @RequestParam("email") String email,
                                       @Length(min = 6, max = 6) @RequestParam("code") String code,
                                       HttpSession session) {
        // 调用业务层方法进行验证
        String s = authorizeService.validateOnly(email, code, session.getId());
        if (s == null) {
            session.setAttribute("reset-password", email); // 验证通过后在session中存储邮箱信息
            return RestBean.success("验证通过，请修改密码");
        } else {
            return RestBean.failure(400, s); // 验证失败，返回错误信息
        }
    }

    /**
     * 忘记密码时，用户提交新密码进行重置
     *
     * @param password 用户提交的新密码
     * @param session 获取当前会话session
     * @return 返回包含结果的RestBean对象
     */
    @RequestMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min = 6, max = 16) @RequestParam("password") String password,
                                          HttpSession session) {
        String email = (String) session.getAttribute("reset-password"); // 从session中获取之前存储的邮箱
        session.removeAttribute("reset-password"); // 重置完成后移除session中的邮箱信息
        if (email == null) {
            return RestBean.failure(400, "请先验证邮箱"); // 如果没有验证邮箱，返回错误信息
        } else if (authorizeService.resetPassword(password, email)) {
            return RestBean.success("密码修改成功"); // 密码修改成功，返回成功消息
        } else {
            return RestBean.failure(500, "内部错误，请联系管理员"); // 修改失败，返回内部错误
        }
    }
}
