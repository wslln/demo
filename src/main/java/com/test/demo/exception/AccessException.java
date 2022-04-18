package com.test.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * 403
 * @author zhout 2022/3/4 6:04 下午
 */
public class AccessException extends BaseException {

    public AccessException() {
        super("403", null, "无权限");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
