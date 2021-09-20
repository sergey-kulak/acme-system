package com.acme.menusrv.menu.repository;

import com.acme.menusrv.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> updatePosition(UUID id, UUID cmpId, UUID ppId, int position) {
        return mongoTemplate.updateFirst(
                query(where("id").is(id)
                        .and("companyId").is(cmpId)
                        .and("publicPointId").is(ppId)),
                update("position", position),
                Category.class)
                .flatMap(r -> Mono.empty());
    }
}
