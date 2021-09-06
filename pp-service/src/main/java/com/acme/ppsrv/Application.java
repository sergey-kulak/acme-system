package com.acme.ppsrv;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

@SpringBootApplication(exclude = JooqAutoConfiguration.class)
@OpenAPIDefinition(info = @Info(
        title = "Public point service APIs", version = "1.0",
        description = "APIs to manage public point data")
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
