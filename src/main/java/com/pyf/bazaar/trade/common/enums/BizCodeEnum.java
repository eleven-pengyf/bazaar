package com.pyf.bazaar.trade.common.enums;

import lombok.Getter;

@Getter
public enum BizCodeEnum {
    SUCCESS(200, "成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "权限不足"),
    SYSTEM_ERROR(500, "系统内部错误"),

    // 用户业务码
    USER_ACCOUNT_DELETED(1001, "该账号已注销，是否立即恢复？"),
    USER_PHONE_EXISTS(1002, "手机号已注册，请直接登录"),
    USER_LOGIN_FAIL(1003, "账号不存在或密码错误"),
    ;

    private final int code;
    private final String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}