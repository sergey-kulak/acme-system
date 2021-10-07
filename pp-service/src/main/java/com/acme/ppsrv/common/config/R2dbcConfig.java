package com.acme.ppsrv.common.config;

import com.acme.ppsrv.order.OrderItemStatus;
import com.acme.ppsrv.order.OrderStatus;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class R2dbcConfig {
    private static List<Object> CONVERTERS = Arrays.asList(
            new EnumWriteSupport<PublicPointStatus>() {
            },
            new EnumWriteSupport<OrderStatus>() {
            },
            new EnumWriteSupport<OrderItemStatus>() {
            }
    );

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(DatabaseClient databaseClient) {
        R2dbcDialect dialect = DialectResolver.getDialect(databaseClient.getConnectionFactory());
        List<Object> converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        converters.addAll(CONVERTERS);
        return new R2dbcCustomConversions(
                CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters),
                Collections.emptyList());
    }
}
