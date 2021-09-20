package com.acme.menusrv.test;

import com.acme.menusrv.common.config.MongoLiquibaseConfig;
import com.acme.menusrv.common.config.ServiceSecurityConfig;
import com.acme.menusrv.common.config.ValidationConfig;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataMongoTest(includeFilters = {
        @ComponentScan.Filter(Service.class),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*MapperImpl")
})
@Import({
        MongoLiquibaseConfig.class,
        ValidationConfig.class,
        ServiceSecurityConfig.class,
        ServiceIntegrationTestConfig.class
})
public class ServiceIntegrationTest {

    static MongoDBContainer MONGODB_CONTAINER =
            new MongoDBContainer(DockerImageName.parse("mongo:5.0.2"));

    static {
        MONGODB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB_CONTAINER::getReplicaSetUrl);
    }
}
