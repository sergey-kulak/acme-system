package com.acme.menusrv.menu.repository;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CategoryRepositoryCustom {
    Mono<Void> updatePosition(UUID id, UUID cmpId, UUID ppId, int position);


}
