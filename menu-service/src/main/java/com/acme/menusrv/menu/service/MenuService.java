package com.acme.menusrv.menu.service;

import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.menu.dto.MenuCategoryDto;
import com.acme.menusrv.menu.dto.MenuDishFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface MenuService {
    Flux<MenuCategoryDto> getMenu(UUID publicPointId);

    Flux<DishDto> findDishes(@Valid MenuDishFilter filter);

    Mono<List<String>> findTags(UUID categoryId);
}
