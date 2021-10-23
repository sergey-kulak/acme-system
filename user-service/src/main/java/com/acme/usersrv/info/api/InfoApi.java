package com.acme.usersrv.info.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ReactiveFeignClient(name = "info-service", path = "/api/info")
public interface InfoApi {

    @GetMapping()
    Mono<String> getInfo();
}
