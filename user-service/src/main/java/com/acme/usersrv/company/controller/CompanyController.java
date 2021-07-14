package com.acme.usersrv.company.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.openapi.ConflictErrorResponse;
import com.acme.usersrv.common.openapi.EntityCreatedResponse;
import com.acme.usersrv.common.openapi.ValidationErrorResponse;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Company API", description = "Company management API")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    @Operation(description = "Register a company")
    @EntityCreatedResponse
    @ValidationErrorResponse
    @ConflictErrorResponse(description = "Company with specified vatin, reg number or full name already registered")
    public Mono<ResponseEntity<IdDto>> register(@RequestBody RegisterCompanyDto registrationDto,
                                                ServerHttpRequest request) {
        return companyService.register(registrationDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

}
