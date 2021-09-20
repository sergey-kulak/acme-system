package com.acme.menusrv.menu.repository;

import com.acme.menusrv.menu.Category;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends ReactiveMongoRepository<Category, UUID>, CategoryRepositoryCustom {
    @Query(value = "{ 'companyId': ?0, 'publicPointId': ?1 }", sort = "{ position : 1 }")
    Flux<Category> findAll(UUID cmpId, UUID ppId);

    @Query(value = "{ 'companyId': ?0, 'publicPointId': ?1 }", delete = true)
    Mono<Void> deleteAll(UUID cmpId, UUID ppId);

    @Query(value = "{ 'companyId': ?0, 'publicPointId': ?1 }", count = true)
    Mono<Long> countAll(UUID cmpId, UUID ppId);

    @Query(value = "{ 'companyId': ?0, 'publicPointId': ?1, id: {'$nin' : ?2} }", delete = true)
    Mono<Void> deleteNotIn(UUID cmpId, UUID ppId, List<UUID> categoryIds);
}
