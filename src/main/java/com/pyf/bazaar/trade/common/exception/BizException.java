package com.pyf.bazaar.trade.common.exception;

import com.pyf.bazaar.trade.common.enums.BizCodeEnum;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMsg());
        this.code = bizCodeEnum.getCode();
    }

    public BizException(String message, int code) {
        super(message);
        this.code = code;
    }
}