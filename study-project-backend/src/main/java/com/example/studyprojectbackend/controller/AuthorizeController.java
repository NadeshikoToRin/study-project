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

// 登录注册
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    // 邮箱正则表达式
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    //用户名称正则表达式
    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
    @Resource
    AuthorizeService authorizeService;

    // 用户注册校验邮箱
    @RequestMapping("/valid-register-email")
    public RestBean<String> validateRegisterEmail(@Pattern(regexp = EMAIL_REGEX)
                                                  @RequestParam("email") String email,
                                                  HttpSession session) {
        String s = authorizeService.sendValidateEmail(email, session.getId(), false);
        if (s == null)
            return RestBean.success("邮件已发送，请注意查收");
        else
            return RestBean.failure(400, s);
    }

    // 用户注册校验邮箱
    @RequestMapping("/valid-reset-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)
                                          @RequestParam("email") String email,
                                          HttpSession session) {
        String s = authorizeService.sendValidateEmail(email, session.getId(), true);
        if (s == null)
            return RestBean.success("邮件已发送，请注意查收");
        else
            return RestBean.failure(400, s);
    }

    @RequestMapping(value = "/verify-saved", method = RequestMethod.POST)
    public RestBean<String> verifySaved(@RequestBody Map<String, String> requestBody) {
        String text = requestBody.get("text");
        if (text == null) {
            return RestBean.failure(400, "缺少参数: text");
        }
        if (!authorizeService.isExist(text)) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, "用户已存在");
        }
    }

    //处理注册逻辑
    @PostMapping("/register")
    public RestBean<String> register(@Pattern(regexp = USERNAME_REGEX) @RequestParam("username") String username,
                                     @Length(min = 6, max = 16) @RequestParam("password") String password,
                                     @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                     @Length(min = 6, max = 6) @RequestParam("code") String code,
                                     HttpSession session) {

        String s = authorizeService.validateAndRegister(username, password, email, code, session.getId());
        if (s == null) {
            return RestBean.success("注册成功!");
        } else {
            return RestBean.failure(400, s);
        }
    }
    //忘记密码

    /**
     * 1.发邮件
     * 2.验证sessionId+email+code是否在redis中
     * 3.修改密码
     */
    @RequestMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX)
                                       @RequestParam("email") String email,
                                       @Length(min = 6, max = 6) @RequestParam("code") String code,
                                       HttpSession session) {

        String s = authorizeService.validateOnly(email, code, session.getId());
        if (s == null) {
            session.setAttribute("reset-password", email);
            return RestBean.success("验证通过，请修改密码");
        } else {
            return RestBean.failure(400, s);
        }
    }

    @RequestMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min = 6, max = 16) @RequestParam("password") String password,
                                          HttpSession session) {
        String email = (String) session.getAttribute("reset-password");
        session.removeAttribute("reset-password");
        if (email == null) {
            return RestBean.failure(400, "请先验证邮箱");
        } else if (authorizeService.resetPassword(password, email)) {
            return RestBean.success("密码修改成功");
        } else {
            return RestBean.failure(500, "内部错误，请联系管理员");
        }
    }
}


