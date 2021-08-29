package com.acme.usersrv.common.config;

import com.acme.commons.feign.SecuredFeignConfig;
import org.springframework.context.annotation.Configuration;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@Configuration
@EnableReactiveFeignClients("com.acme.usersrv")
public class FeignConfig extends SecuredFeignConfig {

}
