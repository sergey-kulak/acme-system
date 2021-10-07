package com.acme.ppsrv.publicpoint.controller;

import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.ppsrv.publicpoint.dto.ClientLoginRequest;
import com.acme.ppsrv.publicpoint.dto.ClientLoginResponse;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTablesDto;
import com.acme.ppsrv.publicpoint.service.PublicPointTableLoginService;
import com.acme.ppsrv.publicpoint.service.PublicPointTableService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/public-point-tables")
@RequiredArgsConstructor
@Tag(name = "Public Point Table Api", description = "Public Point Table Management Api")
public class PublicPointTableController {
    private final PublicPointTableService ppTableService;
    private final PublicPointTableLoginService ppTableLoginService;
    private final Environment env;

    @PostMapping
    @SecureOperation(description = "Save public point tables")
    @ValidationErrorResponse
    public Mono<Void> save(@RequestBody SavePpTablesDto dto) {
        return ppTableService.save(dto);
    }

    @GetMapping
    @SecureOperation(description = "Find public point tables")
    @ApiResponse(responseCode = "200")
    public Flux<PublicPointTableDto> find(@RequestParam UUID publicPointId) {
        return ppTableService.findAll(publicPointId);
    }

    @GetMapping("/{id}")
    @SecureOperation(description = "Find public point table by id")
    @ApiResponse(responseCode = "200")
    public Mono<PublicPointTableDto> findById(@PathVariable UUID id) {
        return ppTableService.findById(id);
    }

    @GetMapping("/count")
    @SecureOperation(description = "Find public point tables")
    @ApiResponse(responseCode = "200")
    public Mono<Long> countAll(@RequestParam UUID publicPointId) {
        return ppTableService.countAll(publicPointId);
    }

    @PostMapping("/login")
    @SecureOperation(description = "Login as client table user")
    @ApiResponse(responseCode = "200")
    public Mono<ClientLoginResponse> login(@RequestBody ClientLoginRequest request) {
        return ppTableLoginService.login(request);
    }

    @GetMapping("/{id}/client-ui-url")
    @SecureOperation(description = "Get public point url")
    @ApiResponse(responseCode = "200")
    public Mono<String> getClientUiUrl(@PathVariable UUID id) {
        return ppTableService.getCode(id)
                .map(this::buildClientUiUrl);
    }

    private String buildClientUiUrl(String code) {
        return String.format(env.getProperty("pp-srv.client-ui-login-url", StringUtils.EMPTY), code);
    }
}
