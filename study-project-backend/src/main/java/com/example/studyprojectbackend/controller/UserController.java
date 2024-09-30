package com.example.studyprojectbackend.controller;

import com.example.studyprojectbackend.entity.RestBean;
import com.example.studyprojectbackend.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * UserController 类负责处理与用户相关的HTTP请求。
 * 通过此控制器，前端可以获取与当前会话关联的用户信息。
 */
@RestController // 标记该类为REST控制器，所有方法都将返回JSON格式的数据，而不是视图
@RequestMapping("/api/user") // 定义基础的请求路径，所有该类中的方法的URL都将以"/api/user"开头
public class UserController {

    /**
     * 获取当前登录的用户信息。
     *
     * @param user 从当前session中获取名为"account"的属性，如果该属性不存在，则该值为null。
     *             SessionAttribute注解用于直接从会话(session)中获取存储的属性，而不需要手动从HttpSession对象中获取。
     *             value = "account" 表示从session中获取key为"account"的对象。
     *             required = false 表示即使session中不存在这个属性，也不会抛出异常，而是会将user设置为null。
     * @return 返回一个RestBean对象，该对象封装了成功状态和当前用户信息。
     *         如果session中存储了用户信息，则返回该用户对象；如果session中没有用户信息，则返回null。
     *         RestBean.success(user) 方法构建了一个成功的响应，其中包含用户数据。
     */
    @GetMapping("/me") // 处理GET请求，路径为"/api/user/me"。通常用于获取资源（此处是获取用户信息）
    public RestBean<AccountUser> me(@SessionAttribute(value = "account", required = false) AccountUser user) {
        // 使用RestBean.success方法构建一个包含用户信息的成功响应
        return RestBean.success(user); // 返回用户信息的成功响应，user可能为null（未登录状态）
    }

}
