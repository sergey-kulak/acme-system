package com.acme.usersrv.company.repository;


import com.acme.usersrv.common.repository.AbstractCustomJooqRepository;
import com.acme.usersrv.common.utils.CollectionUtils;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

import static com.acme.usersrv.jooq.Tables.COMPANY;

public class CompanyRepositoryImpl extends AbstractCustomJooqRepository implements CompanyRepositoryCustom {

    @Override
    public Mono<Page<Company>> find(CompanyFilter filter, Pageable pageable) {
        Query query = buildQuery(filter);
        return page(query, pageable, Company.class);
    }

    private Query buildQuery(CompanyFilter filter) {
        Criteria criteria = Criteria.empty();
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            criteria = criteria.and("status").in(filter.getStatus());
        }
        if (StringUtils.isNotBlank(filter.getNamePattern())) {
            criteria = criteria.and("full_name")
                    .like(filter.getNamePattern() + "%").ignoreCase(true);
        }
        if (StringUtils.isNotBlank(filter.getCountry())) {
            criteria = criteria.and("country")
                    .is(filter.getCountry().toUpperCase());
        }
        if (StringUtils.isNotBlank(filter.getVatin())) {
            criteria = criteria.and("vatin")
                    .is(filter.getVatin().toUpperCase());
        }
        return Query.query(criteria);
    }

    public Mono<Page<Company>> findByJooq(CompanyFilter filter, Pageable pageable) {
        DSLContext dslContext = getDslContext();
        Condition where = buildWhere(filter);

        Mono<Long> count = doCount(dslContext.selectCount()
                .from(COMPANY)
                .where(where));

        return count.flatMap(total -> {
            if (total > 0) {
                return doSelect(dslContext.selectFrom(COMPANY)
                        .where(where)
                        .orderBy(getSortFields(COMPANY, pageable.getSort()))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset()), Company.class)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));

            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    private Condition buildWhere(CompanyFilter filter) {
        Condition where = DSL.noCondition();
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            where = where.and(COMPANY.STATUS.in(filter.getStatus()));
        }
        if (StringUtils.isNotBlank(filter.getNamePattern())) {
            where = where.and(COMPANY.FULL_NAME
                    .likeIgnoreCase(filter.getNamePattern() + "%"));
        }
        if (StringUtils.isNotBlank(filter.getCountry())) {
            where = where.and(COMPANY.COUNTRY
                    .eq(filter.getCountry().toUpperCase()));
        }
        if (StringUtils.isNotBlank(filter.getVatin())) {
            where = where.and(COMPANY.VATIN
                    .eq(filter.getVatin().toUpperCase()));
        }

        return where;
    }

    @Override
    public Flux<CompanyDto> findNames(Collection<CompanyStatus> statuses) {
        return doSelect(getDslContext()
                .select(COMPANY.ID, COMPANY.FULL_NAME, COMPANY.STATUS)
                .from(COMPANY)
                .where(buildWhere(statuses))
                .orderBy(COMPANY.FULL_NAME.asc()), CompanyDto.class);
    }

    private Condition buildWhere(Collection<CompanyStatus> statuses) {
        return CollectionUtils.isEmpty(statuses) ? DSL.noCondition() :
                COMPANY.STATUS.in(statuses);
    }
}
