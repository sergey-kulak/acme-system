package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.jooq.tables.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanCountry;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.CollectionUtils;
import com.acme.commons.utils.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class PlanRepositoryImpl extends AbstractCustomJooqRepository implements PlanRepositoryCustom {
    private static final com.acme.accountingsrv.jooq.tables.Plan PLAN =
            com.acme.accountingsrv.jooq.tables.Plan.PLAN.as("p");
    private static final com.acme.accountingsrv.jooq.tables.PlanCountry PLAN_COUNTRY =
            com.acme.accountingsrv.jooq.tables.PlanCountry.PLAN_COUNTRY.as("pc");
    private static final CompanyPlan COMPANY_PLAN = CompanyPlan.COMPANY_PLAN.as("cp");

    @Override
    public Mono<Map<UUID, List<String>>> getCountries(Collection<UUID> planIds) {
        Flux<PlanCountry> itemFlux = doSelect(getDslContext()
                .selectFrom(PLAN_COUNTRY)
                .where(PLAN_COUNTRY.PLAN_ID.in(planIds)), PlanCountry.class);

        return itemFlux.collectList()
                .map(items -> StreamUtils.groupToListsAndMap(items,
                        PlanCountry::getPlanId, PlanCountry::getCountry));
    }

    @Override
    public Mono<Page<Plan>> find(PlanFilter filter, Pageable pageable) {
        DSLContext dslContext = getDslContext();

        Condition where = buildWhere(dslContext, filter);

        Table<?> table = filter.isOnlyGlobal() ? PLAN.leftJoin(PLAN_COUNTRY)
                .on(PLAN.ID.eq(PLAN_COUNTRY.PLAN_ID)) : PLAN;

        Mono<Long> count = doCount(dslContext.selectCount()
                .from(table)
                .where(where));

        return count.flatMap(total -> {
            if (total > 0) {
                return doSelect(dslContext.selectFrom(table)
                        .where(where)
                        .orderBy(getSortFields(PLAN, pageable.getSort()))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset()), Plan.class)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));

            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    private Condition buildWhere(DSLContext dslContext,
                                 PlanFilter filter) {
        Condition where = DSL.noCondition();
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            where = where.and(PLAN.STATUS.in(filter.getStatus()));
        }
        if (StringUtils.isNotBlank(filter.getNamePattern())) {
            where = where.and(PLAN.NAME
                    .likeIgnoreCase(filter.getNamePattern() + "%"));
        }
        if (filter.getTableCount() != null) {
            where = where.and(PLAN.MAX_TABLE_COUNT
                    .ge(filter.getTableCount()));
        }

        if (filter.isOnlyGlobal()) {
            where = where.and(PLAN_COUNTRY.PLAN_ID.isNull());
        } else {
            if (filter.getCountry() != null) {
                where = where.and(PLAN.ID.in(dslContext.select(PLAN_COUNTRY.PLAN_ID)
                        .from(PLAN_COUNTRY)
                        .where(PLAN_COUNTRY.COUNTRY.eq(filter.getCountry().toUpperCase()))
                ));
            }
        }
        if (filter.getCompanyId() != null) {
            where = where.and(PLAN.ID.eq(dslContext.select(COMPANY_PLAN.PLAN_ID)
                    .from(COMPANY_PLAN)
                    .where(COMPANY_PLAN.COMPANY_ID.eq(filter.getCompanyId())
                            .and(COMPANY_PLAN.END_DATE.isNull()))));
        }

        return where;
    }

}
