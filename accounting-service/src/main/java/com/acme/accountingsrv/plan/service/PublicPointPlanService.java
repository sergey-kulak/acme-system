package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.PublicPointPlanDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
public interface PublicPointPlanService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<UUID> assignPlan(@Valid AssignPlanDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> stopActivePlan(UUID publicPointId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','ACCOUNTANT','PP_MANAGER')")
    Mono<UUID> findActivePlan(UUID publicPointId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'ACCOUNTANT')")
    Mono<List<PublicPointPlanDto>> getHistory(UUID publicPointId);

    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Mono<Map<UUID, Long>> findPlanStatistics(UUID planId);
}
