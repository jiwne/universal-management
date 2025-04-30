package com.jin.java.universalcommon.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一API响应包装类
 *
 * @param <T> 数据负载类型
 * @author jin
 * @since 2025/1/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7571324166150040797L;

    /**
     * 响应代码(默认"0"表示成功)
     */
    private String code = StatusEnum.SUCCESS.getCode();

    /**
     * 响应消息
     */
    private String msg = StatusEnum.SUCCESS.getMsg();

    /**
     * 响应数据负载
     */
    private T data;

    /**
     * 创建新的Result实例
     */
    public static <T> Result<T> of(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    // 成功相关方法
    public static <T> Result<T> success() {
        return of(StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> success(T data) {
        return of(StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success(String msg) {
        return of(StatusEnum.SUCCESS.getCode(), msg, null);
    }

    public static <T> Result<T> success(String msg, T data) {
        return of(StatusEnum.SUCCESS.getCode(), msg, data);
    }

    // 失败相关方法
    public static <T> Result<T> failure() {
        return of(StatusEnum.ERROR.getCode(), StatusEnum.ERROR.getMsg(), null);
    }

    public static <T> Result<T> failure(String msg) {
        return of(StatusEnum.ERROR.getCode(), msg, null);
    }

    public static <T> Result<T> failure(StatusEnum status) {
        return of(status.getCode(), status.getMsg(), null);
    }

    public static <T> Result<T> failure(String code, String msg) {
        return of(code, msg, null);
    }

    public static <T> Result<T> failure(StatusEnum status, String customMsg) {
        return of(status.getCode(), customMsg, null);
    }

    /**
     * 检查结果是否表示成功操作
     */
    public boolean isSuccess() {
        return StatusEnum.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 如果结果成功，返回数据，否则抛出异常
     */
    public T orElseThrow() {
        if (isSuccess()) {
            return data;
        }
        throw new RuntimeException(msg);
    }
}