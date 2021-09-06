package com.acme.accountingsrv.plan.controller;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.PublicPointPlanDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.service.PublicPointPlanService;
import com.acme.accountingsrv.plan.service.PlanService;
import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public-point-plans")
@RequiredArgsConstructor
@Tag(name = "Public Point Plan Api", description = "Public Point Plan Management Api")
public class PublicPointPlanController {
    private final PublicPointPlanService publicPointPlanService;
    private final PlanService planService;

    @PostMapping
    @SecureOperation(description = "Assign plan to public point")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody AssignPlanDto dto,
                                              ServerHttpRequest request) {
        return publicPointPlanService.assignPlan(dto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping("/active")
    @SecureOperation(description = "Get public point active plan")
    public Mono<PlanWithCountriesDto> findActivePlan(@RequestParam UUID publicPointId) {
        return publicPointPlanService.findActivePlan(publicPointId)
                .flatMap(planService::findById);
    }

    @GetMapping("/active/id")
    @SecureOperation(description = "Get public point active plan id")
    public Mono<UUID> findActivePlanId(@RequestParam UUID publicPointId) {
        return publicPointPlanService.findActivePlan(publicPointId);
    }

    @GetMapping("/history")
    @SecureOperation(description = "Get plan change history for public point")
    public Mono<List<PublicPointPlanDto>> getHistory(@RequestParam UUID publicPointId) {
        return publicPointPlanService.getHistory(publicPointId);
    }
}
