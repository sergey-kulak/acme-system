package com.acme.usersrv.common.config;

import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dslContext(ConnectionFactory connectionFactory) {
        R2dbcDialect r2dbcDialect = DialectResolver.getDialect(connectionFactory);
        SQLDialect jooqDialect = translateToJooqDialect(r2dbcDialect);
        Settings settings = new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false);
        return DSL.using(jooqDialect, settings);
    }

    private SQLDialect translateToJooqDialect(R2dbcDialect r2dbcDialect) {
        if (r2dbcDialect instanceof MySqlDialect) {
            return SQLDialect.MYSQL;
        }
        if (r2dbcDialect instanceof PostgresDialect) {
            return SQLDialect.POSTGRES;
        }
        throw new IllegalArgumentException("unsupported r2dbc dialect " + r2dbcDialect.getClass());
    }
}
