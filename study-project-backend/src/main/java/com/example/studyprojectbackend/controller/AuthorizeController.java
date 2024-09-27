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

    // 校验邮箱
    @RequestMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)
                                          @RequestParam("email") String email,
                                          HttpSession session) {
        String s = authorizeService.sendValidateEmail(email, session.getId());
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
}
