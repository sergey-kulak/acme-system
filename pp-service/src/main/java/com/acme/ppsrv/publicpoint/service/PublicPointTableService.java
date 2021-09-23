package com.acme.ppsrv.publicpoint.service;

import com.acme.commons.security.NotAccountantUserAuthenticated;
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
    @NotAccountantUserAuthenticated
    Flux<PublicPointTableDto> findAll(UUID publicPointId);

    @NotAccountantUserAuthenticated
    Mono<String> getCode(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Void> save(@Valid SavePpTablesDto saveDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Long> countAll(UUID publicPointId);
}
