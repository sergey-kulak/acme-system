package com.acme.rfdatasrv.lang.service;

import com.acme.rfdatasrv.lang.Lang;
import com.acme.rfdatasrv.lang.dto.LangDto;
import com.acme.rfdatasrv.lang.mapper.LangMapper;
import com.acme.rfdatasrv.lang.repository.LangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LangServiceImpl implements LangService {
    private final LangRepository langRepository;
    private final LangMapper langMapper;

    @Override
    public Flux<LangDto> findAllActive() {
        return langRepository.findAllActive()
                .map(langMapper::toDto);
    }

    @Override
    public Mono<Void> changeStatus(String code, boolean active) {
        return langRepository.findById(code)
                .map(country -> changeStatus(country, active))
                .flatMap(langRepository::save)
                .then();
    }

    private Lang changeStatus(Lang country, boolean active) {
        country.setActive(active);
        return country;
    }
}
