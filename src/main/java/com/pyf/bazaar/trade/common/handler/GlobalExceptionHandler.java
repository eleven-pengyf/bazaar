package com.pyf.bazaar.trade.common.handler;

import com.pyf.bazaar.trade.common.exception.BizException;
import com.pyf.bazaar.trade.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
    * 参数校验失败（@Valid）：返回 400 + 字段错误列表
    * */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(Exception e) {
        List<Result.FieldError> errors;
        if (e instanceof MethodArgumentNotValidException ex) {
            errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(err -> new Result.FieldError(err.getField(), err.getDefaultMessage()))
                    .collect(Collectors.toList());
        } else {
            BindException ex = (BindException) e;
            errors = ex.getFieldErrors().stream()
                    .map(err -> new Result.FieldError(err.getField(), err.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
        return Result.error(400, "参数校验失败", errors);
    }

    /**
     * 处理业务异常（Service 层抛出的 BizException）
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)  // 业务异常返回 200，通过 code 区分业务状态
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}