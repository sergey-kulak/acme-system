package com.acme.rfdatasrv.country.service;

import com.acme.rfdatasrv.country.Country;
import com.acme.rfdatasrv.country.dto.CountryDto;
import com.acme.rfdatasrv.country.mapper.CountryMapper;
import com.acme.rfdatasrv.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    public Flux<CountryDto> findAllActive() {
        return countryRepository.findAllActive()
                .map(countryMapper::toDto);
    }

    @Override
    public Mono<Void> changeStatus(String code, boolean active) {
        return countryRepository.findById(code)
                .map(country -> changeStatus(country, active))
                .flatMap(countryRepository::save)
                .then();
    }

    private Country changeStatus(Country country, boolean active) {
        country.setActive(active);
        return country;
    }
}
