package com.acme.accountingsrv.common.config;

import com.acme.commons.feign.SecuredFeignConfig;
import org.springframework.context.annotation.Configuration;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@Configuration
@EnableReactiveFeignClients("com.acme.accountingsrv")
public class FeignConfig extends SecuredFeignConfig {

}
