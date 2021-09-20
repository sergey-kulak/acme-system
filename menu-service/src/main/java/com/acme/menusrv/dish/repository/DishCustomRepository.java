package com.acme.menusrv.dish.repository;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface DishCustomRepository {
    Mono<List<String>> findTags(UUID companyId, UUID publicPointId);
}
