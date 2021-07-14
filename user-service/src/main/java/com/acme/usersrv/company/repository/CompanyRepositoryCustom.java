package com.acme.usersrv.company.repository;

import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.dto.CompanyFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface CompanyRepositoryCustom {
    Mono<Page<Company>> find(CompanyFilter filter, Pageable pageable);

    Mono<Page<Company>> findByJooq(CompanyFilter filter, Pageable pageable);
}
