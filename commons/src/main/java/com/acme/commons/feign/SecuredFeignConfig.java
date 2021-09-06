package com.acme.commons.feign;

import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.context.annotation.Bean;

public class SecuredFeignConfig {
    @Bean
    public PageJacksonModule pageJacksonModule() {
        return new PageJacksonModule();
    }

    @Bean
    public AuthFeignInterceptor authFeignInterceptor() {
        return new AuthFeignInterceptor();
    }
}
