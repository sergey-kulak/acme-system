package com.acme.ppsrv.plan.api;

import com.acme.ppsrv.plan.dto.PlanWithCountriesDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ReactiveFeignClient(name = "accounting-service", path = "/api/public-point-plans")
public interface PublicPointPlanApi {

    @GetMapping("/active/id")
    Mono<UUID> findActivePlanId(@RequestParam UUID publicPointId);

    @GetMapping("/active")
    Mono<PlanWithCountriesDto> findActivePlan(@RequestParam UUID publicPointId);
}
