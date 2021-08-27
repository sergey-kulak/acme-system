package com.acme.rfdatasrv.currency.service;

import com.acme.rfdatasrv.currency.Currency;
import com.acme.rfdatasrv.currency.dto.CurrencyDto;
import com.acme.rfdatasrv.currency.mapper.CurrencyMapper;
import com.acme.rfdatasrv.currency.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    public Flux<CurrencyDto> findAllActive() {
        return currencyRepository.findAllActive()
                .map(currencyMapper::toDto);
    }

    @Override
    public Mono<Void> changeStatus(String code, boolean active) {
        return currencyRepository.findById(code)
                .map(country -> changeStatus(country, active))
                .flatMap(currencyRepository::save)
                .then();
    }

    private Currency changeStatus(Currency country, boolean active) {
        country.setActive(active);
        return country;
    }
}
