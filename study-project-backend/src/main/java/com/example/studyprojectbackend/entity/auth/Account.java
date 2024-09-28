package com.example.studyprojectbackend.entity.auth;

import lombok.Data;

//用户对象,映射数据库
@Data
public class Account {
    private  String id;
    private String email;
    private String username;
    private String password;
}
