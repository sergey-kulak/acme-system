package com.acme.usersrv.user.repository;

import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.CollectionUtils;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.UserFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import static com.acme.usersrv.jooq.Tables.USER;

public class UserRepositoryImpl extends AbstractCustomJooqRepository implements UserRepositoryCustom {
    @Override
    public Mono<Page<User>> find(UserFilter filter, Pageable pageable) {
        DSLContext dslContext = getDslContext();
        Condition where = buildWhere(filter);

        Mono<Long> count = doCount(dslContext.selectCount()
                .from(USER)
                .where(where));

        return count.flatMap(total -> {
            if (total > 0) {
                return doSelect(dslContext.selectFrom(USER)
                        .where(where)
                        .orderBy(getSortFields(USER, pageable.getSort()))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset()), User.class)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));

            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    private Condition buildWhere(UserFilter filter) {
        Condition where = DSL.noCondition();
        if (filter.getCompanyId() != null) {
            where = where.and(USER.COMPANY_ID.eq(filter.getCompanyId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            where = where.and(USER.STATUS.in(filter.getStatus()));
        }
        if (CollectionUtils.isNotEmpty(filter.getRole())) {
            where = where.and(USER.ROLE.in(filter.getRole()));
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where = where.and(USER.EMAIL.likeIgnoreCase(filter.getEmail() + "%"));
        }
        return where;
    }
}
