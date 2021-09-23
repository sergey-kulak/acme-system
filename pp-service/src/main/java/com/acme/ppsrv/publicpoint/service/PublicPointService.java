package com.acme.ppsrv.publicpoint.service;

import com.acme.commons.security.UserAuthenticated;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.dto.CreatePublicPointDto;
import com.acme.ppsrv.publicpoint.dto.FullDetailsPublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
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
public interface PublicPointService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<UUID> create(@Valid CreatePublicPointDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<Void> update(UUID id, @Valid UpdatePublicPointDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<Void> changeStatus(UUID id, @NotNull PublicPointStatus newStatus);

    @UserAuthenticated
    Mono<PublicPointDto> findById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<FullDetailsPublicPointDto> findFullDetailsById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER')")
    Mono<Page<FullDetailsPublicPointDto>> find(PublicPointFilter filter, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('ADMIN, COMPANY_OWNER')")
    Flux<PublicPointDto> findNames(UUID companyId);

}
