package com.acme.usersrv.company.service;

import com.acme.usersrv.company.dto.RegisterCompanyDto;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface CompanyService {
    Mono<UUID> register(@Valid RegisterCompanyDto registrationDto);
}
