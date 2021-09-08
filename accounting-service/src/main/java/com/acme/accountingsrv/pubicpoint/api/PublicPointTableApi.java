package com.acme.accountingsrv.pubicpoint.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ReactiveFeignClient(name = "pp-service", path = "/api/public-point-tables")
public interface PublicPointTableApi {

    @GetMapping("/count")
    Mono<Long> countAll(@RequestParam UUID publicPointId);
}
