package com.test.demo.exception;

import com.google.common.base.Strings;
import com.test.demo.utils.MessageUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author zhout 2022/3/4 5:58 下午
 */
public abstract class BaseException extends RuntimeException {

    @Getter
    private final String errorCode;

    @Getter
    private final String errorMessage;

    public BaseException(String code, Object[] params, String message) {
        super(message);
        this.errorCode = Strings.isNullOrEmpty(code) ? "" : code;
        this.errorMessage = MessageUtils.getMessage(this.errorCode, params);
    }

    public BaseException(String code, Object[] params, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = Strings.isNullOrEmpty(code) ? "" : code;
        this.errorMessage = MessageUtils.getMessage(this.errorCode, params);
    }

    public BaseException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 异常状态吗
     *
     * @return http状态码
     */
    public abstract HttpStatus getHttpStatus();
}
