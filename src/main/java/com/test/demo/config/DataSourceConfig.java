package com.test.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author zhout 2022/3/4 5:36 下午
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url.demo}")
    private String url;

    @Value("${spring.datasource.username.demo}")
    private String username;

    @Value("${spring.datasource.password.demo}")
    private String password;

    @Value("${spring.datasource.initial-size:5}")
    private int initialSize;

    @Value("${spring.datasource.max-active:20}")
    private int maxActive;

    @Value("${spring.datasource.min-idle:5}")
    private int minIdle;

    @Value("${spring.datasource.max-wait:10000}")
    private int maxWait;

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();

        // 基本属性
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // 配置连接池的大小
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);

        // 配置获取连接等待超时的时间，单位是毫秒
        dataSource.setMaxWait(maxWait);
        dataSource.setUseUnfairLock(true);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(300000);

        // 配置连接检测的策略，在连接空闲时检测
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);

        // 配置监控统计拦截的filter
        dataSource.setFilters("stat");

        return dataSource;
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
