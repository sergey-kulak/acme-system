package com.acme.accountingsrv.test;

import com.acme.accountingsrv.common.config.JooqConfig;
import com.acme.accountingsrv.common.config.R2dbcConfig;
import com.acme.accountingsrv.common.config.ServiceSecurityConfig;
import com.acme.accountingsrv.common.config.ValidationConfig;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
public @interface ServiceIntegrationTest {
}
