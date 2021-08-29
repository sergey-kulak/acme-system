package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
public interface CompanyPlanService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<UUID> assignPlan(@Valid AssignPlanDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> stopActivePlan(UUID companyId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'ACCOUNTANT')")
    Mono<UUID> findActivePlan(UUID companyId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'ACCOUNTANT')")
    Mono<List<CompanyPlanDto>> getHistory(UUID companyId);

    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Flux<UUID> findCompanyIdsWithPlan(UUID planId);
}
