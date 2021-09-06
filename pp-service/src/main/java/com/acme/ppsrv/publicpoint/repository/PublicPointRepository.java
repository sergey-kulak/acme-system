package com.acme.ppsrv.publicpoint.repository;

import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PublicPointRepository extends ReactiveSortingRepository<PublicPoint, UUID>,
        PublicPointRepositoryCustom {
    @Query("delete from public_point_lang where public_point_id = $1")
    @Modifying
    Mono<Void> clearLangs(UUID ppId);

    @Query("insert into public_point_lang(public_point_id, lang) values($1, $2)")
    @Modifying
    Mono<Void> addLang(UUID ppId, String lang);

    @Query("select id, name, company_id, status " +
            "from public_point " +
            "where company_id = $1 and status <> 'STOPPED' " +
            "order by name asc")
    Flux<PublicPointDto> findNotStoppedByCompanyId(UUID companyId);

    @Query("select id, name, company_id, status " +
            "from public_point " +
            "where id = $1")
    Mono<PublicPointDto> findDtoById(UUID id);
}
