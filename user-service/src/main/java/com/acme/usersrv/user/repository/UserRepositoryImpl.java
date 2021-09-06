package com.acme.usersrv.user.repository;

import com.acme.commons.repository.AbstractCustomJooqRepository;
import com.acme.commons.utils.CollectionUtils;
import com.acme.usersrv.jooq.enums.UserStatus;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.dto.UserNameFilter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class UserRepositoryImpl extends AbstractCustomJooqRepository implements UserRepositoryCustom {
    private static final com.acme.usersrv.jooq.tables.User USER =
            com.acme.usersrv.jooq.tables.User.USER.as("us");

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
        if (CollectionUtils.isNotEmpty(filter.getId())) {
            where = where.and(USER.ID.in(filter.getId()));
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where = where.and(USER.EMAIL.likeIgnoreCase(filter.getEmail() + "%"));
        }
        return where;
    }

    @Override
    public Flux<UserDto> findNames(UserNameFilter filter) {
        DSLContext dslContext = getDslContext();
        Condition where = buildWhere(filter);

        return doSelect(dslContext
                .select(USER.ID, USER.FIRST_NAME, USER.LAST_NAME, USER.EMAIL)
                .from(USER)
                .where(where)
                .orderBy(USER.LAST_NAME.asc()), UserDto.class);
    }

    private Condition buildWhere(UserNameFilter filter) {
        Condition where = USER.COMPANY_ID.eq(filter.getCompanyId())
                .and(USER.ROLE.in(List.of(filter.getRole())))
                .and(USER.STATUS.eq(UserStatus.ACTIVE));

        if (filter.getPublicPointId() != null) {
            where = where.and(USER.PUBLIC_POINT_ID.in(filter.getPublicPointId()));
        }
        return where;
    }


}
