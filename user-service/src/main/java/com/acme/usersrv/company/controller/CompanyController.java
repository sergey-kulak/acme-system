package com.acme.usersrv.company.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.openapi.ConflictErrorResponse;
import com.acme.usersrv.common.openapi.EntityCreatedResponse;
import com.acme.usersrv.common.openapi.EntityNotFoundResponse;
import com.acme.usersrv.common.openapi.OpenApiPage;
import com.acme.usersrv.common.openapi.ValidationErrorResponse;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.CompanyStatusDto;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import com.acme.usersrv.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

    @GetMapping
    @Operation(description = "Find companies with pagination")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyApiPage.class)))
    public Mono<Page<CompanyDto>> find(@ParameterObject CompanyFilter companyFilter,
                                       @ParameterObject Pageable pageable) {
        return companyService.find(companyFilter, pageable);
    }

    @PutMapping("/{id}/status")
    @Operation(description = "Change company status")
    @ApiResponse(responseCode = "400", description = "Validation errors or not allowed status change", content = @Content(schema = @Schema(hidden = true)))
    @EntityNotFoundResponse
    public Mono<Void> changeStatus(@PathVariable UUID id, @RequestBody CompanyStatusDto statusDto) {
        return companyService.changeStatus(id, statusDto.getStatus());
    }

    @GetMapping("/{id}")
    @Operation(description = "Find company by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<CompanyDto> findById(@PathVariable UUID id) {
        return companyService.findById(id);
    }

    @GetMapping("/{id}/full-details")
    @Operation(description = "Find company with full details by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<FullDetailsCompanyDto> findFullDetailsById(@PathVariable UUID id) {
        return companyService.findFullDetailsById(id);
    }

    @PutMapping("/{id}")
    @Operation(description = "Update company")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody UpdateCompanyDto dto) {
        return companyService.update(id, dto);
    }

    @Schema(name = "Company page")
    private static class CompanyApiPage extends OpenApiPage<CompanyDto> {
    }
}
