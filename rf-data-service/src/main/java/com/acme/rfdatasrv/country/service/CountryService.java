package com.acme.rfdatasrv.country.service;

import com.acme.rfdatasrv.country.dto.CountryDto;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {
    Flux<CountryDto> findAllActive();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> changeStatus(String code, boolean active);
}
