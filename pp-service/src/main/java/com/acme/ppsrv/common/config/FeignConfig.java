package com.acme.ppsrv.common.config;

import com.acme.commons.feign.SecuredFeignConfig;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@Configuration
@EnableReactiveFeignClients("com.acme.ppsrv")
public class FeignConfig extends SecuredFeignConfig {

}
