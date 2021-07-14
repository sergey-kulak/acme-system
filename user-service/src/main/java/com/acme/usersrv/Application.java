package com.acme.usersrv;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

@SpringBootApplication(exclude = JooqAutoConfiguration.class)
@OpenAPIDefinition(info = @Info(
        title = "User service APIs", version = "1.0",
        description = "APIs to manage companies and users")
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
