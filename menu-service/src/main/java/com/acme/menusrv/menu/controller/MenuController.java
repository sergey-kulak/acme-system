package com.acme.menusrv.menu.controller;

import com.acme.commons.openapi.EntityNotFoundResponse;
import com.acme.commons.openapi.SecureOperation;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.MenuCategoryDto;
import com.acme.menusrv.menu.dto.MenuDishFilter;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateMenuDto;
import com.acme.menusrv.menu.service.MenuService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/menu")
@Tag(name = "Menu Api", description = "Menu Api")
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/categories")
    @SecureOperation(description = "Get categories")
    public Flux<MenuCategoryDto> getMenu() {
        return menuService.getMenuCategories();
    }

    @GetMapping("/dishes")
    @SecureOperation(description = "Get dishes of a category")
    public Flux<DishDto> findDishes(@RequestParam UUID categoryId) {
        return menuService.findDishes(categoryId);
    }

}
