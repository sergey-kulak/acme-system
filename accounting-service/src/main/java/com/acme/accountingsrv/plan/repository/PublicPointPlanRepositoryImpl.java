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

public class PublicPointPlanRepositoryImpl extends AbstractCustomJooqRepository
        implements PublicPointPlanRepositoryCustom {
    private static final com.acme.accountingsrv.jooq.tables.PublicPointPlan PP_PLAN =
            com.acme.accountingsrv.jooq.tables.PublicPointPlan.PUBLIC_POINT_PLAN.as("pp");

    @Override
    public Mono<Map<UUID, Long>> getPublicPointCount(Collection<UUID> planIds) {
        Flux<PlanCount> itemFlux = doSelect(getDslContext()
                .select(PP_PLAN.PLAN_ID, count())
                .from(PP_PLAN)
                .where(PP_PLAN.PLAN_ID.in(planIds)
                        .and(PP_PLAN.END_DATE.isNull()))
                .groupBy(PP_PLAN.PLAN_ID), PlanCount.class);

        return itemFlux.collectList()
                .map(items -> StreamUtils.mapToMap(items, PlanCount::getPlanId, PlanCount::getCount));
    }

    @Data
    private static class PlanCount {
        private UUID planId;
        private long count;
    }
}
