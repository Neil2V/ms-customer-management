package com.pacifico.customer.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    @Bean
    public DataSource liquibaseDataSource(Environment env) {
        log.info("=== Creando DataSource para Liquibase ===");

        HikariDataSource dataSource = DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .url(env.getProperty("spring.datasource.url"))
                .username(env.getProperty("spring.datasource.username"))
                .password(env.getProperty("spring.datasource.password"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                .build();

        // Configuración mínima para Liquibase (solo se usa al inicio)
        dataSource.setMaximumPoolSize(2);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(30000);
        dataSource.setPoolName("LiquibasePool");

        log.info("DataSource creado: {}", dataSource.getJdbcUrl());

        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource liquibaseDataSource,
                                     LiquibaseProperties properties) {
        log.info("=== Inicializando Liquibase ===");
        log.info("ChangeLog: {}", properties.getChangeLog());
        log.info("Default Schema: {}", properties.getDefaultSchema());
        log.info("Liquibase Schema: {}", properties.getLiquibaseSchema());

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(liquibaseDataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());

        if (properties.getParameters() != null) {
            liquibase.setChangeLogParameters(properties.getParameters());
        }

        log.info("=== Liquibase configurado correctamente ===");

        return liquibase;
    }
}