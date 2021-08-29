package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.plan.Plan;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface PlanRepository extends ReactiveSortingRepository<Plan, UUID>, PlanRepositoryCustom {
    @Query("delete from plan_country where plan_id = $1")
    @Modifying
    Mono<Void> clearCountries(UUID planId);

    @Query("insert into plan_country(plan_id, country) values($1, $2)")
    @Modifying
    Mono<Void> addCountry(UUID planId, String country);

    @Query("select p.* from plan p " +
            "left join plan_country pc on pc.plan_id = p.id " +
            "where status = 'ACTIVE' and (pc.plan_id is null or pc.country = $1)")
    Flux<Plan> findActiveByCountry(String country);
}
