package com.acme.menusrv.menu.service.impl;

import com.acme.commons.security.CompanyUserDetails;
import com.acme.commons.security.SecurityUtils;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.mapper.DishMapper;
import com.acme.menusrv.dish.repository.DishRepository;
import com.acme.menusrv.menu.Category;
import com.acme.menusrv.menu.dto.MenuCategoryDto;
import com.acme.menusrv.menu.dto.MenuDishFilter;
import com.acme.menusrv.menu.mapper.CategoryMapper;
import com.acme.menusrv.menu.repository.CategoryRepository;
import com.acme.menusrv.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;
    private final CategoryMapper mapper;
    private final DishMapper dishMapper;

    @Override
    public Flux<MenuCategoryDto> getMenuCategories() {
        return SecurityUtils.getCurrentUser()
                .flatMapMany(this::findCategoriesAndFilter);
    }

    private Flux<MenuCategoryDto> findCategoriesAndFilter(CompanyUserDetails client) {
        return categoryRepository.findAll(client.getCompanyId(), client.getPublicPointId())
                .filter(this::filterByTime)
                .map(mapper::toMenuDto);
    }

    private boolean filterByTime(Category ctg) {
        LocalDateTime now = LocalDateTime.now();
        return (ctg.getDays().isEmpty() || ctg.getDays().contains(now.getDayOfWeek()))
                && (ctg.getStartTime() == null || !ctg.getStartTime().isAfter(now.toLocalTime()))
                && (ctg.getEndTime() == null || !now.toLocalTime().isAfter(ctg.getEndTime()));
    }

    @Override
    public Flux<DishDto> findDishes(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .flatMap(ctg -> SecurityUtils.isPpAccessible(ctg.getCompanyId(), ctg.getPublicPointId())
                        .thenReturn(ctg))
                .flatMapMany(ctg -> dishRepository.findAllById(ctg.getDishIds()))
                .map(dishMapper::toDto);
    }

    @Override
    public Mono<List<String>> findTags(UUID categoryId) {
        return null;
    }
}
