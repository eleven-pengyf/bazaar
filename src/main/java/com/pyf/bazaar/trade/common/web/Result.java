package com.pyf.bazaar.trade.common.web;

import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private List<FieldError> errors;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
    public static <T> Result<T> error(Integer code, String msg, List<FieldError> errors) {
        Result<T> result = new Result<>(code, msg, null);
        result.setErrors(errors);
        return result;
    }

    // 内部类：字段错误
    @Data
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}