package com.acme.accountingsrv.plan.repository;

import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.StreamUtils;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static org.jooq.impl.DSL.count;

public class CompanyPlanRepositoryImpl extends AbstractCustomJooqRepository
        implements CompanyPlanRepositoryCustom {
    private static final com.acme.accountingsrv.jooq.tables.CompanyPlan COMPANY_PLAN =
            com.acme.accountingsrv.jooq.tables.CompanyPlan.COMPANY_PLAN.as("cp");

    @Override
    public Mono<Map<UUID, Long>> getCompanyCount(Collection<UUID> planIds) {
        Flux<PlanCount> itemFlux = doSelect(getDslContext()
                .select(COMPANY_PLAN.PLAN_ID, count())
                .from(COMPANY_PLAN)
                .where(COMPANY_PLAN.PLAN_ID.in(planIds)
                        .and(COMPANY_PLAN.END_DATE.isNull()))
                .groupBy(COMPANY_PLAN.PLAN_ID), PlanCount.class);

        return itemFlux.collectList()
                .map(items -> StreamUtils.mapToMap(items, PlanCount::getPlanId, PlanCount::getCount));
    }

    @Data
    private static class PlanCount {
        private UUID planId;
        private long count;
    }
}
