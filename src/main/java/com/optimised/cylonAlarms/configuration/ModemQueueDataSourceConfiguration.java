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
@EnableJpaRepositories(basePackages = "com.optimised.cylonAlarms.repository.alarmsToModemQueue",
        entityManagerFactoryRef = "modemQueueEntityManagerFactory",
        transactionManagerRef= "modemQueueTransactionManager")
public class ModemQueueDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.modemqueue")
    public DataSourceProperties modemQueueDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean
    @ConfigurationProperties("spring.datasource.modemqueue.configuration")
    public DataSource modemQueueDataSource() {
        return modemQueueDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }
    @Bean(name = "modemQueueEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean modemQueueEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(modemQueueDataSource())
                .packages("com.optimised.cylonAlarms.model.alarmsToModemQueue")
                .build();
    }
    @Bean
    public PlatformTransactionManager modemQueueTransactionManager(
            final @Qualifier("modemQueueEntityManagerFactory") LocalContainerEntityManagerFactoryBean modemQueueEntityManagerFactory) {
        return new JpaTransactionManager(modemQueueEntityManagerFactory.getObject());
    }
}
