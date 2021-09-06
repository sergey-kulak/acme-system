package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.plan.PublicPointPlan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PublicPointPlanRepository extends ReactiveSortingRepository<PublicPointPlan, UUID>,
        PublicPointPlanRepositoryCustom {
    @Query("select * from public_point_plan where public_point_id = $1 and end_date is null")
    Mono<PublicPointPlan> findActivePlan(UUID publicPointId);

    @Query("select plan_id from public_point_plan where public_point_id = $1 and end_date is null")
    Mono<PlanIdOnly> findActivePlanId(UUID publicPointId);

    Flux<PublicPointPlan> findByPublicPointIdOrderByEndDate(UUID publicPointId);

    @Query("select company_id, count(1) as pp_count " +
            "from public_point_plan " +
            "where plan_id = $1 and end_date is null " +
            "group by company_id")
    Flux<CompanyPpCount> findPlanStatistics(UUID planId);
}