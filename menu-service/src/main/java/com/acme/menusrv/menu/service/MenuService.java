package com.acme.menusrv.menu.service;

import com.acme.commons.security.ClientAuthenticated;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.menu.dto.MenuCategoryDto;
import com.acme.menusrv.menu.dto.MenuDishFilter;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
public interface MenuService {
    @ClientAuthenticated
    Flux<MenuCategoryDto> getMenuCategories();

    @ClientAuthenticated
    Flux<DishDto> findDishes(UUID categoryId);

    @ClientAuthenticated
    Mono<List<String>> findTags(UUID categoryId);
}
