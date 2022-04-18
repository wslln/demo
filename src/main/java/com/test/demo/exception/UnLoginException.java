package com.test.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * 401
 *
 * @author zhout 2022/3/4 6:06 下午
 */
@Deprecated
public class UnLoginException extends BaseException {

    public UnLoginException() {
        super("401", null, "未登陆");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
