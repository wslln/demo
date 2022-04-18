package com.test.demo.utils;

import com.test.demo.helper.ApplicationContextHelper;
import lombok.Setter;
import org.springframework.context.MessageSource;

import java.util.Objects;

/**
 * @author zhout 2022/3/4 5:59 下午
 */
public class MessageUtils {

    @Setter
    public static MessageSource messageSource;

    public static String getMessage(String code, Object[] params) {
        return Objects.isNull(messageSource) ? "" : messageSource.getMessage(code, params, ApplicationContextHelper.getLocale());
    }

    public static String getMessage(String code) {
        return  Objects.isNull(messageSource) ? "" : messageSource.getMessage(code, new Object[0], ApplicationContextHelper.getLocale());
    }
}
