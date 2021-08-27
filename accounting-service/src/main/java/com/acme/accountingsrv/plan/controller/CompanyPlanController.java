package com.acme.accountingsrv.plan.controller;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.service.CompanyPlanService;
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
@RequestMapping("/api/company-plans")
@RequiredArgsConstructor
@Tag(name = "Company Plan Api", description = "Company Plan Management Api")
public class CompanyPlanController {
    private final CompanyPlanService companyPlanService;
    private final PlanService planService;

    @PostMapping
    @SecureOperation(description = "Assign plan to company")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody AssignPlanDto dto,
                                              ServerHttpRequest request) {
        return companyPlanService.assignPlan(dto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping("/active")
    public Mono<PlanWithCountriesDto> findActivePlan(@RequestParam UUID companyId) {
        return companyPlanService.findActivePlan(companyId)
                .flatMap(planService::findById);
    }

    @GetMapping("/history")
    public Mono<List<CompanyPlanDto>> getHistory(@RequestParam UUID companyId) {
        return companyPlanService.getHistory(companyId);
    }
}
