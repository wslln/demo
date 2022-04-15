package com.test.demo.config;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhout 2022/3/4 5:30 下午
 */
public class ApplicationEnvConfig implements EnvironmentPostProcessor {

    private final String ENV_IS_CONSUL = "ENV_DEMO_IS_CONSUL";
    private final String ENV_CONSUL_TOKEN = "ENV_DEMO_CONSUL_TOKEN";
    private final String ENV_CONSUL_HOST = "ENV_DEMO_CONSUL_HOST";
    private final String ENV_CONSUL_PORT = "ENV_DEMO_CONSUL_PORT";
    private final String ENV_CONSUL_KEY = "ENV_DEMO_CONSUL_KEY";

    private static final Map<String, Object> props = Maps.newLinkedHashMap();

    private String getProp(String key) {
        String value = System.getProperty(key);
        return Strings.isNullOrEmpty(value) ? System.getenv(key) : value;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!Objects.equals("true", getProp(ENV_IS_CONSUL))) {
            System.out.println("未开启Consul支持");
            return;
        }

        if (CollectionUtils.isEmpty(props)) {
            // 读取Consul配置
            String configToken = getProp(ENV_CONSUL_TOKEN);
            String configHost = getProp(ENV_CONSUL_HOST);
            String configPort = getProp(ENV_CONSUL_PORT);
            String configKey = getProp(ENV_CONSUL_KEY);

            // 参数检查
            if (Strings.isNullOrEmpty(configHost) || Strings.isNullOrEmpty(configPort) || Strings.isNullOrEmpty(configKey)) {
                System.err.println("Consul的配置不正确，token=" + configToken + "，host=" + configHost + "，port=" + configPort + "，key=" + configKey);
                System.exit(1);
            }

            // 读取配置
            System.out.println("读取Consul配置：");
            Map<String, String> headers = Maps.newHashMap();
            headers.put("X-Consul-Token", configToken);
            try {
                URL url = new URL("http", configHost, Integer.parseInt(configPort), "");
                Consul client = Consul.builder()
                        .withUrl(url)
                        .withHeaders(headers)
                        .withConnectTimeoutMillis(60000L)
                        .withReadTimeoutMillis(60000L)
                        .build();
                KeyValueClient kvClient = client.keyValueClient();
                List<String> keys = kvClient.getKeys(configKey);

                for (String key : keys) {
                    Optional<String> value = kvClient.getValueAsString(key);
                    String[] str = key.split("/");
                    String k = str[str.length - 1];
                    String v = value.orElse("");
                    System.out.println(k + ": " + v);
                    props.put(k, v);
                }


                // 替换配置中的 @{domain}和@{env}
                String domainName = (String) props.get("scrm.domain");
                String env = (String) props.get("project.platform");
                props.entrySet().stream().peek(e -> {
                    String value = (String) e.getValue();
                    if (value != null && value.contains("@{domain}")) {
                        if (domainName != null) {
                            value = value.replaceAll("@\\{domain}", domainName);
                        }
                    }
                    if (value != null && value.contains("@{env}")) {
                        if (env != null) {
                            value = value.replaceAll("@\\{env}", env);
                        }
                    }
                    e.setValue(value);
                }).forEach(e ->
                        props.put(e.getKey(), e.getValue())
                );


            } catch (Exception e) {
                System.err.println("从Consul读取配置失败：" + e.getMessage());
                System.exit(1);
            }
        }

        // 写入配置
        environment.getPropertySources().addFirst(new MapPropertySource("Consul", props));
    }
}
