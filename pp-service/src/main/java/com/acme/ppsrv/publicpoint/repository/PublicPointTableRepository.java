package com.acme.ppsrv.publicpoint.repository;

import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PublicPointTableRepository
        extends ReactiveSortingRepository<PublicPointTable, UUID> {
    Mono<Long> countByPublicPointId(UUID ppId);

    Flux<PublicPointTable> findByPublicPointIdOrderByName(UUID ppId);

    <T> Flux<T> findByPublicPointIdOrderByName(UUID ppId, Class<T> clazz);

    @Modifying
    @Query("delete from public_point_table " +
            "where id in (:ids) and public_point_id = :ppId")
    Mono<Void> deleteByPublicPointId(List<UUID> ids, UUID ppId);
}
