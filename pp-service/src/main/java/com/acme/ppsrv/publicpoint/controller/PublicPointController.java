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
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
import com.acme.ppsrv.publicpoint.service.PublicPointService;
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
@RequestMapping("/api/public-points")
@RequiredArgsConstructor
@Tag(name = "Public Point Api", description = "Public Point Management Api")
public class PublicPointController {
    private final PublicPointService ppService;

    @PostMapping
    @SecureOperation(description = "Create a public point")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreatePublicPointDto saveDto,
                                              ServerHttpRequest request) {
        return ppService.create(saveDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping("{id}")
    @SecureOperation(description = "Find public point by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<PublicPointDto> findById(@PathVariable UUID id) {
        return ppService.findById(id);
    }

    @GetMapping("{id}/full-details")
    @SecureOperation(description = "Find public point full details by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<FullDetailsPublicPointDto> findByIdFullDetails(@PathVariable UUID id) {
        return ppService.findFullDetailsById(id);
    }

    @PutMapping("/{id}")
    @SecureOperation(description = "Update public point")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody UpdatePublicPointDto dto) {
        return ppService.update(id, dto);
    }

    @GetMapping
    @SecureOperation(description = "Find public points with pagination")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = PublicPointApiPage.class)))
    public Mono<Page<FullDetailsPublicPointDto>> find(@ParameterObject PublicPointFilter filter,
                                                      @ParameterObject Pageable pageable) {
        return ppService.find(filter, pageable);
    }

    @PutMapping("/{id}/status")
    @SecureOperation(description = "Change public point status")
    @ApiResponse(responseCode = "400", description = "Validation errors or not allowed status change", content = @Content(schema = @Schema(hidden = true)))
    @EntityNotFoundResponse
    public Mono<Void> changeStatus(@PathVariable UUID id, @RequestBody PublicPointStatusDto statusDto) {
        return ppService.changeStatus(id, statusDto.getStatus());
    }

    @GetMapping("/names")
    @SecureOperation(description = "Find public points list for company")
    @ApiResponse(responseCode = "200")
    public Flux<PublicPointDto> findNames(@RequestParam UUID companyId) {
        return ppService.findNames(companyId);
    }

    @Schema(name = "public point page")
    private static class PublicPointApiPage extends OpenApiPage<FullDetailsPublicPointDto> {
    }
}
