package com.acme.rfdatasrv.common.config;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.integration.spring.SpringResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class MongoLiquibaseConfig {
    @Autowired
    private LiquibaseProperties properties;
    @Autowired
    private ResourceLoader resourceLoader;

    @SneakyThrows
    @Bean
    public Liquibase liquibaseRunner(MongoLiquibaseDatabase database) {
        Liquibase liquiBase = new Liquibase(properties.getChangeLog(),
                new SpringResourceAccessor(resourceLoader), database);
        liquiBase.update(properties.getContexts());

        return liquiBase;
    }

    @SneakyThrows
    @Bean
    public MongoLiquibaseDatabase database() {
        return (MongoLiquibaseDatabase) DatabaseFactory.getInstance()
                .openDatabase(properties.getUrl(), null, null, null, null);
    }

}
