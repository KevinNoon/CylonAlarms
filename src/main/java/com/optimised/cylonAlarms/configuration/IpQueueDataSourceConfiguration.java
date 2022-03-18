package com.optimised.cylonAlarms.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.optimised.cylonAlarms.repository.alarmsToIPQueue",
        entityManagerFactoryRef = "ipQueueEntityManagerFactory",
        transactionManagerRef= "ipQueueTransactionManager")
public class IpQueueDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.ipqueue")
    public DataSourceProperties ipQueueDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean
    @ConfigurationProperties("spring.datasource.ipqueue.configuration")
    public DataSource ipQueueDataSource() {
        return ipQueueDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }
    @Bean(name = "ipQueueEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ipQueueEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(ipQueueDataSource())
                .packages("com.optimised.cylonAlarms.model.alarmsToIPQueue")
                .build();
    }
    @Bean
    public PlatformTransactionManager ipQueueTransactionManager(
            final @Qualifier("ipQueueEntityManagerFactory") LocalContainerEntityManagerFactoryBean ipQueueEntityManagerFactory) {
        return new JpaTransactionManager(ipQueueEntityManagerFactory.getObject());
    }
}
