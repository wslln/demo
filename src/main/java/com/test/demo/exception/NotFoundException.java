package com.test.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * @author zhout 2022/3/4 6:07 下午
 */
public class NotFoundException extends BaseException {

    public NotFoundException() {
        super("404", null, "资源找不到");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
