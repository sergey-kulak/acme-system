package com.acme.menusrv.dish.repository;

import com.acme.menusrv.dish.Dish;
import com.acme.menusrv.dish.dto.DishNameDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DishRepository extends ReactiveMongoRepository<Dish, UUID>, DishCustomRepository {

    @Query(value = "{ 'companyId': ?0, 'publicPointId': ?1, deleted: false }", sort = "{ name : 1 }")
    Flux<DishNameDto> findActiveNames(UUID companyId, UUID publicPointId);
}
