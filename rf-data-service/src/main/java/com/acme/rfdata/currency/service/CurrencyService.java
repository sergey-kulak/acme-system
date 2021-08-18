package com.acme.rfdata.currency.service;

import com.acme.rfdata.currency.dto.CurrencyDto;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyService {
    Flux<CurrencyDto> findAllActive();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> changeStatus(String code, boolean active);
}
