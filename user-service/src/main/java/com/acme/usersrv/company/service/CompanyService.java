package com.acme.usersrv.company.service;

import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;

@Validated
public interface CompanyService {
    Mono<UUID> register(@Valid RegisterCompanyDto registrationDto);

    @PreAuthorize("hasAuthority('ADMIN')")
    Mono<Page<FullDetailsCompanyDto>> find(CompanyFilter filter, Pageable pageable);

    @PreAuthorize("hasAuthority('ADMIN')")
    Mono<Page<FullDetailsCompanyDto>> findByJooq(CompanyFilter filter, Pageable pageable);

    @PreAuthorize("hasAuthority('ADMIN')")
    Mono<Void> changeStatus(UUID id, @NotNull CompanyStatus status);

    @PreAuthorize("isAuthenticated()")
    Mono<CompanyDto> findById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_OWNER','PP_MANAGER')")
    Mono<FullDetailsCompanyDto> findFullDetailsById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_OWNER')")
    Mono<Void> update(UUID id, @Valid UpdateCompanyDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN, ACCOUNTANT')")
    Flux<CompanyDto> findNames(Collection<CompanyStatus> status);
}
