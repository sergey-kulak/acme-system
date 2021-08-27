package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.plan.CompanyPlan;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CompanyPlanRepository extends ReactiveSortingRepository<CompanyPlan, UUID>,
        CompanyPlanRepositoryCustom {
    Mono<CompanyPlan> findByCompanyIdAndEndDateNull(UUID companyId);

    Flux<CompanyPlan> findByCompanyIdOrderByEndDate(UUID companyId);
}
