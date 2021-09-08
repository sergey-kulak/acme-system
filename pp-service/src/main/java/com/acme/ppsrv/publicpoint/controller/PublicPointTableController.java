package com.acme.ppsrv.publicpoint.controller;

import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.OpenApiPage;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
import com.acme.ppsrv.publicpoint.dto.CreatePublicPointDto;
import com.acme.ppsrv.publicpoint.dto.FullDetailsPublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import com.acme.ppsrv.publicpoint.dto.PublicPointStatusDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTablesDto;
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
import com.acme.ppsrv.publicpoint.service.PublicPointService;
import com.acme.ppsrv.publicpoint.service.PublicPointTableService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/count")
    @SecureOperation(description = "Find public point tables")
    @ApiResponse(responseCode = "200")
    public Mono<Long> countAll(@RequestParam UUID publicPointId) {
        return ppTableService.countAll(publicPointId);
    }
}
