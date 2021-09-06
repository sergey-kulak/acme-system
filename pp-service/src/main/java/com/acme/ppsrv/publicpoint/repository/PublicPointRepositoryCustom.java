package com.acme.ppsrv.publicpoint.repository;

import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PublicPointRepositoryCustom {
    default Mono<List<String>> getLangs(UUID id) {
        return getLangs(List.of(id))
                .map(map -> map.getOrDefault(id, List.of()));
    }

    Mono<Map<UUID, List<String>>> getLangs(Collection<UUID> ids);

    Mono<Page<PublicPoint>> find(PublicPointFilter filter, Pageable pageable);

}
