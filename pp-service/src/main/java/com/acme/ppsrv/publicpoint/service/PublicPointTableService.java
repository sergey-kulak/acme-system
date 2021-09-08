package com.acme.ppsrv.publicpoint.service;

import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTablesDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface PublicPointTableService {
    @PreAuthorize("isAuthenticated()")
    Flux<PublicPointTableDto> findAll(UUID publicPointId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Void> save(@Valid SavePpTablesDto saveDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Long> countAll(UUID publicPointId);
}
