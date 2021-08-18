package com.acme.rfdata.currency.controller;

import com.acme.commons.openapi.SecureOperation;
import com.acme.rfdata.common.dto.StatusDto;
import com.acme.rfdata.currency.dto.CurrencyDto;
import com.acme.rfdata.currency.service.CurrencyService;
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
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency Api", description = "Currency management Api")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    @Operation(description = "Find all active currency")
    public Flux<CurrencyDto> findAllActive() {
        return currencyService.findAllActive();
    }

    @PutMapping("/{code}")
    @SecureOperation(description = "Change currency status")
    @ApiResponse(responseCode = "200")
    public Mono<Void> changeStatus(@PathVariable String code, @RequestBody StatusDto statusDto) {
        return currencyService.changeStatus(code, statusDto.isActive());
    }
}
