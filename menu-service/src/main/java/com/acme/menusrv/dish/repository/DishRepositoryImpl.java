package com.acme.menusrv.dish.repository;

import com.acme.menusrv.dish.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class DishRepositoryImpl implements DishCustomRepository {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<List<String>> findTags(UUID companyId, UUID publicPointId) {
        return mongoTemplate.query(Dish.class)
                .distinct("tags")
                .matching(query(where("companyId").is(companyId)
                        .and("publicPointId").is(publicPointId)
                        .and("deleted").is(false)))
                .as(String.class)
                .all()
                .collectList();
    }
}
