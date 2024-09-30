package com.example.studyprojectbackend.interceptor;

import com.example.studyprojectbackend.entity.user.AccountUser;
import com.example.studyprojectbackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * AuthorizeInterceptor类是一个拦截器，用于在每个请求之前执行用户权限验证逻辑。
 * 通过实现HandlerInterceptor接口，该拦截器可以在请求处理之前执行特定的逻辑。
 */
@Component // 标记此类为Spring组件，以便自动注入到Spring上下文中
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource // 注入UserMapper对象，用于在数据库中查找用户信息
    UserMapper userMapper;

    /**
     * preHandle方法在每个请求处理之前调用，负责从认证上下文中获取当前用户，并从数据库中查找该用户的信息。
     * 将用户信息存储在session中，以便在后续的请求中使用。
     *
     * @param request  当前的HttpServletRequest对象，表示客户端的请求
     * @param response 当前的HttpServletResponse对象，表示服务器的响应
     * @param handler  当前的处理器，通常是Controller中的某个方法
     * @return 返回true表示请求继续执行，返回false则终止请求的处理
     * @throws Exception 可能会抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从SecurityContext中获取当前的安全上下文（保存了认证信息）
        SecurityContext context = SecurityContextHolder.getContext();
        // 从安全上下文中获取当前认证对象（包含了用户的认证信息）
        Authentication authentication = context.getAuthentication();
        // 从认证对象中获取用户主体（通常是UserDetails的实现类）
        User user = (User) authentication.getPrincipal(); // 获取当前已认证的用户对象

        // 获取当前用户的用户名（此用户名可以是登录时使用的用户名或邮箱）
        String username = user.getUsername();

        // 通过用户名或邮箱从数据库中查找用户的完整信息（AccountUser对象包含更多用户详细信息）
        AccountUser account = userMapper.findAccountUserByNameOrEmail(username);

        // 将查找到的用户信息存储在当前的会话（session）中，以便后续处理或其他请求中使用
        request.getSession().setAttribute("account", account);

        // 返回true，表示继续处理请求
        return true;
    }
}
