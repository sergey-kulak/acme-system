package com.acme.menusrv.menu.controller;

import com.acme.commons.dto.IdDto;
import com.acme.commons.openapi.EntityCreatedResponse;
import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.SecureOperation;
import com.acme.commons.openapi.ValidationErrorResponse;
import com.acme.commons.utils.ResponseUtils;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.FullDetailsDishDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.CreateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateMenuDto;
import com.acme.menusrv.menu.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/categories")
@Tag(name = "Category Api", description = "Category management Api")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(description = "Create a category")
    @EntityCreatedResponse
    @ValidationErrorResponse
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreateCategoryDto dto, ServerHttpRequest request) {
        return categoryService.create(dto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }

    @GetMapping
    @SecureOperation(description = "Find categories")
    public Flux<CategoryDto> find(@RequestParam UUID companyId, @RequestParam UUID publicPointId) {
        return categoryService.findAll(companyId, publicPointId);
    }

    @GetMapping("/{id}")
    @SecureOperation(description = "Find category by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<CategoryDto> findById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    @SecureOperation(description = "Update category")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody UpdateCategoryDto dto) {
        return categoryService.update(id, dto);
    }

    @PostMapping("/order")
    @SecureOperation(description = "Update category order")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> delete(@RequestBody UpdateMenuDto dto) {
        return categoryService.update(dto);
    }

}
