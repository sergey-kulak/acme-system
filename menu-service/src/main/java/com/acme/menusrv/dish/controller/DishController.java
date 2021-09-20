package com.acme.menusrv.dish.controller;

import com.acme.menusrv.dish.dto.CreateDishDto;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.DishFilter;
import com.acme.menusrv.dish.dto.DishNameDto;
import com.acme.menusrv.dish.dto.FullDetailsDishDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import com.acme.menusrv.dish.service.DishService;
import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.ConflictErrorResponse;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.OpenApiPage;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dishes")
@Tag(name = "Dish Api", description = "Dish management Api")
public class DishController {
    private final DishService dishService;

    @PostMapping
    @Operation(description = "Create a dish")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreateDishDto dto, ServerHttpRequest request) {
        return dishService.create(dto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping
    @SecureOperation(description = "Find dishes")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = DishApiPage.class)))
    public Mono<Page<DishDto>> find(@ParameterObject DishFilter filter,
                                    @ParameterObject Pageable pageable) {
        return dishService.find(filter, pageable);
    }

    @GetMapping("/{id}")
    @SecureOperation(description = "Find dish by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<DishDto> findById(@PathVariable UUID id) {
        return dishService.findById(id);
    }

    @GetMapping("/{id}/full-details")
    @SecureOperation(description = "Find dish with full details by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<FullDetailsDishDto> findFullDetailsById(@PathVariable UUID id) {
        return dishService.findFullDetailsById(id);
    }

    @PutMapping("/{id}")
    @SecureOperation(description = "Update dish")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody UpdateDishDto dto) {
        return dishService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @SecureOperation(description = "Delete dish")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> delete(@PathVariable UUID id) {
        return dishService.delete(id);
    }

    @GetMapping("/tags")
    @SecureOperation(description = "Find all tags")
    @ApiResponse(responseCode = "200")
    public Mono<List<String>> findTags(@RequestParam UUID companyId, @RequestParam UUID publicPointId) {
        return dishService.findTags(companyId, publicPointId);
    }

    @GetMapping("/names")
    @SecureOperation(description = "Find public points list for company")
    @ApiResponse(responseCode = "200")
    public Flux<DishNameDto> findNames(@RequestParam UUID companyId, @RequestParam UUID publicPointId) {
        return dishService.findNames(companyId, publicPointId);
    }

    @Schema(name = "Dish page")
    private static class DishApiPage extends OpenApiPage<DishDto> {
    }
}
