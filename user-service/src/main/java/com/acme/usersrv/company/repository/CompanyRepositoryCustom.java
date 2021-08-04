package com.acme.usersrv.company.repository;

import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface CompanyRepositoryCustom {
    Mono<Page<Company>> find(CompanyFilter filter, Pageable pageable);

    Mono<Page<Company>> findByJooq(CompanyFilter filter, Pageable pageable);

    Flux<CompanyDto> findNames(Collection<CompanyStatus> statuses);
}
