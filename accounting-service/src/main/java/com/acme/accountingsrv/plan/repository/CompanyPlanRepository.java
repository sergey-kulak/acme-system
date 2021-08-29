package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.plan.CompanyPlan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CompanyPlanRepository extends ReactiveSortingRepository<CompanyPlan, UUID>,
        CompanyPlanRepositoryCustom {
    @Query("select * from company_plan where company_id = $1 and end_date is null")
    Mono<CompanyPlan> findActiveCompanyPlan(UUID companyId);

    @Query("select plan_id from company_plan where company_id = $1 and end_date is null")
    Mono<PlanIdOnly> findActivePlanId(UUID companyId);

    Flux<CompanyPlan> findByCompanyIdOrderByEndDate(UUID companyId);

    @Query("select company_id from company_plan where plan_id = $1 and end_date is null")
    Flux<CompanyIdOnly> findByPlanId(UUID planId);
}