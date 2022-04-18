package com.test.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * 400，业务异常
 *
 * @author zhout 2022/3/4 6:14 下午
 */
public class BusinessException extends BaseException {

    public BusinessException(String code, String message) {
        super(code, null, message);
    }

    public BusinessException(String code, Object[] params, String message) {
        super(code, params, message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
