package com.acme.usersrv.test;

import com.acme.usersrv.common.config.ServiceSecurityConfig;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataR2dbcTest
@ComponentScan(
        basePackages = "com.acme.usersrv",
        includeFilters = @ComponentScan.Filter(Service.class)
)
@ContextConfiguration(classes = {
        ServiceSecurityConfig.class,
        ServiceIntegrationTestConfig.class
})
public @interface ServiceIntegrationTest {
}
