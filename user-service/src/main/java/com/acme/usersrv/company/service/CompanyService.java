package com.acme.usersrv.company.service;

import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Validated
public interface CompanyService {
    Mono<UUID> register(@Valid RegisterCompanyDto registrationDto);

    Mono<Page<CompanyDto>> find(CompanyFilter filter, Pageable pageable);

    Mono<Page<CompanyDto>> findByJooq(CompanyFilter filter, Pageable pageable);

    Mono<Void> changeStatus(UUID id, @NotNull CompanyStatus status);

    Mono<CompanyDto> findById(UUID id);

    Mono<FullDetailsCompanyDto> findFullDetailsById(UUID id);

    Mono<Void> update(UUID id, @Valid UpdateCompanyDto dto);
}
