package com.jin.java.universalcommon.response;

import lombok.Getter;

/**
 * @author：jin
 * @date：2025/4/30
 */
@Getter
public enum StatusEnum {
    /**
     * 成功
     */
    SUCCESS("0", "成功"),

    /**
     * 用户无权限！
     */
    LOGIN_ERROR1("403", "用户无权限！"),

    /**
     * 未查询到用户信息！
     */
    LOGIN_ERROR2("0101", "未查询到用户信息！"),

    /**
     * 下载失败
     */
    DOWNLOAD("0201", "下载失败"),

    /**
     *  文件过大
     */
    FIND_FILE_ERROR2("0202", "文件过大!"),

    /**
     * 异常
     */
    ERROR("1", "服务器异常"),
    NOT_FOUND_INSTANCE("404","没有找到该服务"),
    ;





    /**
     * 错误编码
     */
    private final String code;
    /**
     * 错误信息
     */
    private final String msg;

    StatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
