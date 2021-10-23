package com.acme.apigw;

import com.acme.apigw.filter.SleuthPostFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class Application {

    @Bean
    public GlobalFilter sleuthPostFilter() {
        return new SleuthPostFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
