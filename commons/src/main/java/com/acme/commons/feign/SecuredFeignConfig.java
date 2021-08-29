package com.acme.commons.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SecuredFeignConfig {

    @Bean
    public AuthFeignInterceptor authFeignInterceptor() {
        return new AuthFeignInterceptor();
    }
}
