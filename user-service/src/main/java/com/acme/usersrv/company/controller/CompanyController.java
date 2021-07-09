package com.acme.usersrv.company.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public Mono<ResponseEntity<IdDto>> register(@RequestBody RegisterCompanyDto registrationDto,
                                                ServerHttpRequest request) {
        return companyService.register(registrationDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

}
