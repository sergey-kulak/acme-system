package com.acme.accountingsrv.plan.repository;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface CompanyPlanRepositoryCustom {
    Mono<Map<UUID, Long>> getCompanyCount(Collection<UUID> planIds);
}
