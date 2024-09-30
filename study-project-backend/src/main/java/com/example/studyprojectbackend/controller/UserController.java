package com.example.studyprojectbackend.controller;

import com.example.studyprojectbackend.entity.RestBean;
import com.example.studyprojectbackend.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser user){
        return RestBean.success(user);
    }
}
