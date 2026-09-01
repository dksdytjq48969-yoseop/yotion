package com.yo.yotion.global.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }



}
