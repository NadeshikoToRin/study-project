package com.example.studyprojectbackend.entity;

import lombok.Data;

@Data
//泛型：接收参数多变
public class RestBean<T> {
    private int status;
    private boolean success;
    private T message;

    public RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    /**
     * 创建一个表示成功响应的RestBean对象
     * 该方法用于当操作成功但不需要返回具体数据时
     *
     * @param <T> 泛型标记，表示数据类型
     * @return 返回一个状态码为200、成功标记为true且数据体为空的RestBean对象
     */
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, true, null);
    }

    /**
     * 创建一个表示成功响应并包含具体数据的RestBean对象
     * 该方法用于当操作成功并需要返回具体数据时
     *
     * @param <T>  泛型标记，表示数据类型
     * @param data 要返回的具体数据
     * @return 返回一个状态码为200、成功标记为true且包含具体数据的RestBean对象
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, true, data);
    }

    /**
     * 创建一个表示失败响应的RestBean对象
     * 该方法用于当操作失败且不需要返回具体数据时
     *
     * @param <T>    泛型标记，表示数据类型
     * @param status 错误状态码
     * @return 返回一个状态码为指定错误码、成功标记为false且数据体为空的RestBean对象
     */
    public static <T> RestBean<T> failure(int status) {
        return new RestBean<>(status, false, null);
    }

    /**
     * 创建一个表示失败响应并包含具体数据的RestBean对象
     * 该方法用于当操作失败但需要返回具体数据时
     *
     * @param <T>    泛型标记，表示数据类型
     * @param status 错误状态码
     * @param data   要返回的具体数据
     * @return 返回一个状态码为指定错误码、成功标记为false且包含具体数据的RestBean对象
     */
    public static <T> RestBean<T> failure(int status, T data) {
        return new RestBean<>(status, false, data);
    }
}
