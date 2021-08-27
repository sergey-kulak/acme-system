package com.acme.accountingsrv.plan.repository;

import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlanRepositoryCustom {
    default Mono<List<String>> getCountries(UUID planId) {
        return getCountries(Collections.singletonList(planId))
                .map(map -> map.getOrDefault(planId, Collections.emptyList()));
    }

    Mono<Map<UUID, List<String>>> getCountries(Collection<UUID> planIds);

    Mono<Page<Plan>> find(PlanFilter filter, Pageable pageable);
}
