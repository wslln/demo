package com.test.demo.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author chnzxg
 * @date 2020/11/13 5:00 下午
 */
public class ValidateUtils {

    //public static final String PATTERN_NAME = "[A-Za-z0-9]+";
    // 字母、数字、中划线、下划线
    public static final String PATTERN_NAME = "[A-Za-z0-9-_]{1,30}";

    public static boolean isEmpty(String params) {
        return !StringUtils.isNoneBlank(params) || StringUtils.isBlank(params);
    }

    public static boolean isEmpty(Integer params) {
        return params == null;
    }

    public static boolean isEmpty(Double params) {
        return params == null;
    }

    public static boolean isEmpty(Long params) {
        return params == null;
    }

    public static boolean isEmpty(Date params) {
        return params == null;
    }

    public static boolean isEmpty(Map params) {
        return params == null || params.size() == 0;
    }

    public static boolean isNotEmpty(String params) {
        return !isEmpty(params);
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    public static boolean isNotEmpty(Collection c) {
        return !isEmpty(c);
    }

    public static boolean isNotEmpty(Integer params) {
        return !isEmpty(params);
    }

    public static boolean isNotEmpty(Long params) {
        return !isEmpty(params);
    }

    public static boolean isNotEmpty(Date params) {
        return !isEmpty(params);
    }

    public static boolean isNotEmpty(Map params) {
        return params != null && params.size() != 0;
    }

    public static boolean validateFlag(Integer params) {
        if (params == null) {
            return false;
        }
        return Objects.equals(params, 1) || Objects.equals(params, 0);
    }

    public static boolean validateFlag(String params) {
        if (isEmpty(params)) {
            return false;
        }
        return StringUtils.isNumeric(params) && validateFlag(Integer.parseInt(params));
    }

    public static boolean validateType(Integer params, Integer... validates) {
        for (Integer validate : validates) {
            if (params == validate) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateType(String params, String... validates) {
        for (String validate : validates) {
            if (validate.equals(params)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateUserName(String name) {
        return Pattern.matches(PATTERN_NAME, name);
    }
}
