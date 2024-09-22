package com.example.studyprojectbackend.entity;

import lombok.Data;

//用户对象
@Data
public class Account {
    private  String id;
    private String email;
    private String username;
    private String password;
}
