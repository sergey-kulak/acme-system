package com.acme.accountingsrv.plan.controller;

import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import com.acme.accountingsrv.plan.dto.PlanStatusDto;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.accountingsrv.plan.service.PlanService;
import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.OpenApiPage;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Plan Api", description = "Plan management Api")
public class PlanController {
    private final PlanService planService;

    @PostMapping
    @SecureOperation(description = "Create a plan")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody SavePlanDto saveDto,
                                              ServerHttpRequest request) {
        return planService.create(saveDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping("{id}")
    @SecureOperation(description = "Find plan by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<PlanWithCountriesDto> findById(@PathVariable UUID id) {
        return planService.findById(id);
    }

    @PutMapping("/{id}")
    @SecureOperation(description = "Update plan")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody SavePlanDto dto) {
        return planService.update(id, dto);
    }

    @GetMapping
    @SecureOperation(description = "Find plans with pagination")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = PlanApiPage.class)))
    public Mono<Page<PlanWithCountDto>> find(@ParameterObject PlanFilter filter,
                                             @ParameterObject Pageable pageable) {
        return planService.find(filter, pageable);
    }

    @PutMapping("/{id}/status")
    @SecureOperation(description = "Change plan status")
    @ApiResponse(responseCode = "400", description = "Validation errors or not allowed status change", content = @Content(schema = @Schema(hidden = true)))
    @EntityNotFoundResponse
    public Mono<Void> changeStatus(@PathVariable UUID id, @RequestBody PlanStatusDto statusDto) {
        return planService.changeStatus(id, statusDto.getStatus());
    }

    @GetMapping("/active")
    @Operation(description = "Find all active plans (global and for country)")
    @ApiResponse(responseCode = "200")
    public Mono<List<PlanDto>> findActive(@RequestParam String country) {
        return planService.findActive(country);
    }

    @Schema(name = "Plan page")
    private static class PlanApiPage extends OpenApiPage<PlanWithCountDto> {
    }
}
