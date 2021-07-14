package com.acme.usersrv.common.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

public class AbstractCustomR2dbcRepository {
    private R2dbcEntityOperations entityOperations;

    public <T> Mono<Page<T>> page(Query query, Pageable pageable, Class<T> clazz) {
        Mono<Long> count = entityOperations.count(query, clazz);
        return count.flatMap(total -> {
            if (total > 0) {
                return entityOperations.select(query.with(pageable), clazz)
                        .collectList()
                        .map(data -> new PageImpl<>(data, pageable, total));
            } else {
                return Mono.just(Page.empty());
            }
        });
    }

    public R2dbcEntityOperations getEntityOperations() {
        return entityOperations;
    }

    @Autowired
    public void setEntityOperations(R2dbcEntityOperations entityOperations) {
        this.entityOperations = entityOperations;
    }
}
