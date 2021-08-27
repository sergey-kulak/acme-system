package com.acme.rfdatasrv.country.controller;

import com.acme.commons.openapi.SecureOperation;
import com.acme.rfdatasrv.common.dto.StatusDto;
import com.acme.rfdatasrv.country.dto.CountryDto;
import com.acme.rfdatasrv.country.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Country Api", description = "Country management Api")
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    @Operation(description = "Find all active countries")
    public Flux<CountryDto> findAllActive() {
        return countryService.findAllActive();
    }

    @PutMapping("/{code}")
    @SecureOperation(description = "Change country status")
    @ApiResponse(responseCode = "200")
    public Mono<Void> changeStatus(@PathVariable String code, @RequestBody StatusDto statusDto) {
        return countryService.changeStatus(code, statusDto.isActive());
    }
}
