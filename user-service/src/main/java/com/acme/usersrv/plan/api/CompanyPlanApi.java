package com.acme.usersrv.plan.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ReactiveFeignClient(name = "accounting-service",
        path = "/api/company-plans")
public interface CompanyPlanApi {

    @GetMapping("/active/id")
    Mono<UUID> findActivePlanId(@RequestParam UUID companyId);
}
