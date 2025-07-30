package com.alibaba.nexus.cart.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
public class SeataDataSourceProxyConfig {

    /**
     * 创建一个 HikariDataSource bean
     * @param dataSourceProperties Spring Boot自动配置的数据源属性
     * @return HikariDataSource 实例
     */
    @Bean
    public DataSource hikariDataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * 使用 Seata 的 DataSourceProxy 代理我们自己的数据源
     * @Primary 注解保证当存在多个 DataSource Bean 时，优先使用这个代理过的数据源
     * @param hikariDataSource 上一步创建的原始数据源
     * @return 代理后的数据源
     */
    @Primary
    @Bean("dataSource")
    public DataSourceProxy dataSourceProxy(DataSource hikariDataSource) {
        // 创建并返回 Seata 的数据源代理，第一个参数是原始数据源
        return new DataSourceProxy(hikariDataSource);
    }
}