package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
public interface CompanyPlanService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<UUID> assignPlan(@Valid AssignPlanDto dto);

    Mono<UUID> findActivePlan(UUID companyId);

    Mono<List<CompanyPlanDto>> getHistory(UUID companyId);
}
