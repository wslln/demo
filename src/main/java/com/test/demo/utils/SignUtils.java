package com.test.demo.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zhout 2022/3/23 2:07 下午
 */
@Slf4j
@UtilityClass
public class SignUtils {

    public static String getStringToSign(String httpMethod, String domain, String path, Map<String, String> signMap) {
        StringBuilder result = new StringBuilder();

        TreeMap<String, String> treeMap = new TreeMap<>(signMap);
        result.append(httpMethod).append(domain).append(path).append("?");
        treeMap.forEach((k, v) -> result.append(k).append("=").append(v).append("&"));

        return result.substring(0, result.length() - 1);
    }

    public static String sign(String signString, String accessSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signData = mac.doFinal(signString.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printBase64Binary(signData);
        } catch (Exception e) {
            log.error("sign signString:{}, accessSecret:{}, error:", signString, accessSecret, e);
        }
        return null;
    }

    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return value == null ? null : URLEncoder.encode(value, "UTF-8");
    }
}
