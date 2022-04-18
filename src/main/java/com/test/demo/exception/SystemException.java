package com.test.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * @author zhout 2022/3/4 6:08 下午
 */
public class SystemException extends BaseException {

    public SystemException(String message) {
        super("500", null, message);
    }

    public SystemException(String message, Throwable throwable) {
        super(null, null, message, throwable);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
