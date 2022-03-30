package com.optimised.cylonAlarms.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(basePackages = "com.optimised.cylonAlarms.repository.queueToAlarm",
        entityManagerFactoryRef = "alarmEntityManagerFactory",
        transactionManagerRef= "alarmTransactionManager")
public class AlarmDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.alarm")
    public DataSourceProperties alarmDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean
    @ConfigurationProperties("spring.datasource.alarm.configuration")
    public DataSource alarmDataSource() {
        return alarmDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }
    @Primary
    @Bean(name = "alarmEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean alarmEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(alarmDataSource());
        em.setPackagesToScan("com.optimised.cylonAlarms.model.queueToAlarm");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("spring.jpa.hibernate.hbm2ddl.auto", "update");
        properties.put("spring.jpa.hibernate.dialect", "org.hibernate.dialect.SQLServer2008Dialect");
        properties.put("spring.jpa.generate-ddl","true");
        em.setJpaPropertyMap(properties);

        return em;
    }
    @Bean
    public PlatformTransactionManager alarmTransactionManager(
            final @Qualifier("alarmEntityManagerFactory") LocalContainerEntityManagerFactoryBean alarmEntityManagerFactory) {
        return new JpaTransactionManager(alarmEntityManagerFactory.getObject());
    }
}
