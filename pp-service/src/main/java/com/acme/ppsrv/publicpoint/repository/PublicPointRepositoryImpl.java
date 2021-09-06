package com.acme.ppsrv.publicpoint.repository;

import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.CollectionUtils;
import com.acme.commons.utils.StreamUtils;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointLang;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
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

public class PublicPointRepositoryImpl extends AbstractCustomJooqRepository
        implements PublicPointRepositoryCustom {
    private static final com.acme.ppsrv.jooq.tables.PublicPoint PUBLIC_POINT =
            com.acme.ppsrv.jooq.tables.PublicPoint.PUBLIC_POINT.as("pp");
    private static final com.acme.ppsrv.jooq.tables.PublicPointLang PUBLIC_POINT_LANG =
            com.acme.ppsrv.jooq.tables.PublicPointLang.PUBLIC_POINT_LANG.as("pl");

    @Override
    public Mono<Map<UUID, List<String>>> getLangs(Collection<UUID> ids) {
        Flux<PublicPointLang> langFlux = doSelect(getDslContext()
                        .selectFrom(PUBLIC_POINT_LANG)
                        .where(PUBLIC_POINT_LANG.PUBLIC_POINT_ID.in(ids)),
                PublicPointLang.class);

        return langFlux.collectList()
                .map(items -> StreamUtils.groupToListsAndMap(items,
                        PublicPointLang::getPublicPointId, PublicPointLang::getLang));
    }

    @Override
    public Mono<Page<PublicPoint>> find(PublicPointFilter filter, Pageable pageable) {
        DSLContext dslContext = getDslContext();

        Condition where = buildWhere(filter);

        Mono<Long> count = doCount(dslContext.selectCount()
                .from(PUBLIC_POINT)
                .where(where));

        return count.flatMap(total -> {
            if (total > 0) {
                return doSelect(dslContext.selectFrom(PUBLIC_POINT)
                        .where(where)
                        .orderBy(getSortFields(PUBLIC_POINT, pageable.getSort()))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset()), PublicPoint.class)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));

            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    private Condition buildWhere(PublicPointFilter filter) {
        Condition where = DSL.noCondition();
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            where = where.and(PUBLIC_POINT.STATUS.in(filter.getStatus()));
        }
        if (StringUtils.isNotBlank(filter.getNamePattern())) {
            where = where.and(PUBLIC_POINT.NAME
                    .likeIgnoreCase(filter.getNamePattern() + "%"));
        }

        if (filter.getCompanyId() != null) {
            where = where.and(PUBLIC_POINT.COMPANY_ID.eq(filter.getCompanyId()));
        }

        return where;
    }

}
