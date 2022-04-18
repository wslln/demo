package com.test.demo.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * pgsql数据库使用json字段时转义处理工具
 *
 * @author jiangyang
 * @date 2020/07/29
 */
public class JsonUtil {

    private static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 解析内容，将转义字符进行转义
     *
     * @param value
     */
    public static String parseValue(String value) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("\\\\", "\\\\\\\\");
        value = value.replaceAll("\\\"", "\\\\\"");
        Matcher m = p.matcher(value);
        value = m.replaceAll("");
        return value;

    }

    /**
     * 解析内容，将转义字符进行转义（引号和反斜杠）,将tab符号转换成"\t"字符串（表中存的是字符串，客户无感）
     *
     * @param value
     */
    public static String escape(String value) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("\\\\", "\\\\\\\\");
        value = value.replaceAll("\\\"", "\\\\\"");
        value = value.replaceAll("\t", "\\\\\\\\t");
        return value;
    }

    /**
     * 将特殊符号进行转义（百分号，下划线，反斜杠）
     *
     * @param value
     */
    public static String escapeForLike(String value) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("\\\\", "\\\\\\\\");
        // 下划线，反斜杠
        value = value.replaceAll("\\%", "\\\\%");
        value = value.replaceAll("\\_", "\\\\_");
        return value;
    }


    /**
     * 将特殊符号进行转义（下划线，反斜杠）
     *
     * @param value
     */
    public static String escapeLike(String value) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        value = value.replaceAll("\\\"", "\\\\\"");
        value = value.replaceAll("\t", "\\\\\\\\t");
        value = value.replaceAll("\\%", "\\\\%");
        value = value.replaceAll("\\_", "\\\\_");
        return value;
    }

    /**
     * 将特殊符号进行转义（反斜线,中文引号）
     *
     * @param value
     */
    public static String escapeSpecial(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replace("\\", "\\\\");
        value = value.replace("\"", "\\\"");
        return value;

    }

    /**
     * 针对手机号查询的特殊处理
     *
     * @param value
     */
    public static String escapeForTel(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        //空格，逗号，双引号进行过滤掉（手机号含有），不过滤就会查询到不该查到的数据
        value = value.replaceAll(" ", "");
        value = value.replaceAll(",", "");
        value = value.replaceAll("\"", "");
        //下划线，百分号,反斜杠进行转义
        value = value.replaceAll("\\%", "\\\\%");
        value = value.replaceAll("\\_", "\\\\_");
        value = value.replaceAll("\\\\", "\\\\\\\\");
        return value;
    }

}
