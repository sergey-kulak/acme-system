package com.acme.rfdata.lang.service;

import com.acme.rfdata.lang.dto.LangDto;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LangService {
    Flux<LangDto> findAllActive();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Mono<Void> changeStatus(String code, boolean active);
}
