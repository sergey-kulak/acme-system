package com.acme.ppsrv.publicpoint.repository;

import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.ClientLoginResponse;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Modifying
    @Query("update public_point_table " +
            "set name = $3, description = $4, seat_count = $5 " +
            "where id = $1 and public_point_id = $2")
    Mono<Void> update(UUID id, UUID ppId, String name, String description, int seatCount);

    Mono<PublicPointTable> findByCode(String code);
}
