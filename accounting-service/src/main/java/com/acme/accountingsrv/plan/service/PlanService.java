package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Validated
public interface PlanService {
    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Mono<UUID> create(@Valid SavePlanDto createDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Mono<Void> update(UUID id, @Valid SavePlanDto createDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Mono<Void> changeStatus(UUID id, @NotNull PlanStatus newStatus);

    @PreAuthorize("isAuthenticated()")
    Mono<PlanWithCountriesDto> findById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','ACCOUNTANT')")
    Mono<Page<PlanWithCountDto>> find(PlanFilter filter, Pageable pageable);

    Mono<List<PlanDto>> findActive(String country);

}
