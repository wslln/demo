package com.test.demo.exception.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.demo.exception.BaseException;
import com.test.demo.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhout 2022/3/7 11:52 上午
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Object> errorHandler(BaseException ex, HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        if (ex instanceof SystemException) {
            log(request, ex, requestId);
        }
        return new ResponseEntity<>(this.body(ex, requestId), HttpStatus.OK);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleExceptionInternal(@Nullable Exception ex, @Nullable Object body, @Nullable HttpHeaders headers, HttpStatus status, @Nullable WebRequest request) {
        if (HttpStatus.BAD_REQUEST.equals(status)) {
            body = this.body(HttpStatus.BAD_REQUEST.value(), "缺少必选参数或参数格式错误");
        }
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(@Nullable HttpRequestMethodNotSupportedException ex, @Nullable HttpHeaders headers, @Nullable HttpStatus status, WebRequest request) {
        log.error("不支持的请求方式");
        return new ResponseEntity<>(this.body(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方式"), HttpStatus.OK);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nullable HttpHeaders headers, @Nullable HttpStatus status, @Nullable WebRequest request) {
        List<String> errors = Lists.newArrayList();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ":" + error.getDefaultMessage());
        }
        Map<String, Object> body = this.body(HttpStatus.BAD_REQUEST.value(), Joiner.on("; ").join(errors));
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    private Map<String, Object> body(Integer code, String message) {
        Map<String, Object> body = Maps.newHashMap();
        body.put("requestId", UUID.randomUUID().toString());
        body.put("code", code);
        body.put("message", message);
        return body;
    }

    private Map<String, Object> body(BaseException ex, String requestId) {
        Map<String, Object> body = Maps.newHashMap();
        body.put("requestId", requestId);
        body.put("code", !Strings.isNullOrEmpty(ex.getErrorCode()) ? ex.getErrorCode() : ex.getHttpStatus().value() + "");
        body.put("message", ex.getErrorMessage());
        return body;
    }

    private void log(HttpServletRequest request, Exception e, String requestId) {
        // 未知异常
        try {
            Map<String, Object> reqInfo = Maps.newHashMap();
            reqInfo.put("id", requestId);
            reqInfo.put("parameters", request.getParameterMap());
            reqInfo.put("requestURI", request.getRequestURI());
            reqInfo.put("locale", request.getLocale());
            log.error("未处理异常：{}", JSON.toJSONString(reqInfo), e);
        } catch (Exception ex) {
            log.error("记录未知异常日志错误", ex);
        }
    }
}
