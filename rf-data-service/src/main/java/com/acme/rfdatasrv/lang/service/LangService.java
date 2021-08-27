package com.acme.rfdatasrv.lang.service;

import com.acme.rfdatasrv.lang.dto.LangDto;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LangService {
    Flux<LangDto> findAllActive();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> changeStatus(String code, boolean active);
}
