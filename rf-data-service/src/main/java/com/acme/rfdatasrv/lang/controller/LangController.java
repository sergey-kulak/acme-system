package com.acme.rfdatasrv.lang.controller;

import com.acme.commons.openapi.SecureOperation;
import com.acme.rfdatasrv.common.dto.StatusDto;
import com.acme.rfdatasrv.lang.dto.LangDto;
import com.acme.rfdatasrv.lang.service.LangService;
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
@RequestMapping("/api/langs")
@RequiredArgsConstructor
@Tag(name = "Language Api", description = "Language management Api")
public class LangController {
    private final LangService langService;

    @GetMapping
    @Operation(description = "Find all active languages")
    public Flux<LangDto> findAllActive() {
        return langService.findAllActive();
    }

    @PutMapping("/{code}")
    @SecureOperation(description = "Change language status")
    @ApiResponse(responseCode = "200")
    public Mono<Void> changeStatus(@PathVariable String code, @RequestBody StatusDto statusDto) {
        return langService.changeStatus(code, statusDto.isActive());
    }
}
