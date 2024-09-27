package com.example.studyprojectbackend.mapper;

import com.example.studyprojectbackend.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 根据用户名或邮箱查询用户
    @Select("select * from db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    //注册用户
    @Insert("insert into db_account(email,username,password) values(#{email},#{username},#{password})")
    int createAccount(String username, String email, String password);
}
