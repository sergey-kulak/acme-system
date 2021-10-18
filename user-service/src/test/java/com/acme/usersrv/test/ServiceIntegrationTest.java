package com.acme.usersrv.test;

import com.acme.usersrv.common.config.JooqConfig;
import com.acme.usersrv.common.config.R2dbcConfig;
import com.acme.usersrv.common.config.ServiceSecurityConfig;
import com.acme.usersrv.common.config.ValidationConfig;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@DataR2dbcTest(includeFilters = {
        @ComponentScan.Filter(Service.class),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*MapperImpl")
})
@Import({
        JooqConfig.class,
        R2dbcConfig.class,
        ValidationConfig.class,
        ServiceSecurityConfig.class,
        ServiceIntegrationTestConfig.class
})
public class ServiceIntegrationTest {

    static PostgreSQLContainer PG_CONTAINER =
            new PostgreSQLContainer(DockerImageName.parse("postgres:13.3-alpine"));

    static {
        PG_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        String jdbUrl = PG_CONTAINER.getJdbcUrl();
        registry.add("spring.liquibase.url", () -> jdbUrl);
        registry.add("spring.liquibase.user", PG_CONTAINER::getUsername);
        registry.add("spring.liquibase.password", PG_CONTAINER::getPassword);
        registry.add("spring.r2dbc.url", () -> jdbUrl.replaceAll("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", PG_CONTAINER::getUsername);
        registry.add("spring.r2dbc.password", PG_CONTAINER::getPassword);
    }
}
