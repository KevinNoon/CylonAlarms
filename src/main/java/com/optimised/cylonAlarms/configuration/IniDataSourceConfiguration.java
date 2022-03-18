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
@EnableJpaRepositories(basePackages = "com.optimised.cylonAlarms.repository.iniFilesToDB",
        entityManagerFactoryRef = "iniEntityManagerFactory",
        transactionManagerRef= "iniTransactionManager")
public class IniDataSourceConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.ini")
    public DataSourceProperties iniDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.ini.configuration")
    public DataSource iniDataSource() {
        return iniDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "iniEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean iniEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(iniDataSource());
        em.setPackagesToScan("com.optimised.cylonAlarms.model.iniFilesToDB");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("spring.jpa.hibernate.hbm2ddl.auto", "update");
        properties.put("spring.jpa.hibernate.dialect", "org.hibernate.dialect.SQLServer2016Dialect");
        properties.put("spring.jpa.generate-ddl","true");
        properties.put("spring.jpa.show-sql","true");
        em.setJpaPropertyMap(properties);

        return em;
    }

//    @Primary
//    @Bean(name = "iniEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean iniEntityManagerFactory(
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(iniDataSource())
//                .packages("com.optimised.cylonAlarms.model.iniFilesToDB")
//                .build();
//    }

    @Primary
    @Bean
    public PlatformTransactionManager iniTransactionManager(
            final @Qualifier("iniEntityManagerFactory") LocalContainerEntityManagerFactoryBean iniEntityManagerFactory) {
        return new JpaTransactionManager(iniEntityManagerFactory.getObject());
    }
}
