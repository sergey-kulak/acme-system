package com.acme.menusrv.dish.service.impl;

import com.acme.menusrv.dish.Dish;
import com.acme.menusrv.dish.dto.CreateDishDto;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.DishFilter;
import com.acme.menusrv.dish.dto.DishNameDto;
import com.acme.menusrv.dish.dto.FullDetailsDishDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import com.acme.menusrv.dish.mapper.DishMapper;
import com.acme.menusrv.dish.repository.DishRepository;
import com.acme.menusrv.dish.service.DishService;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishRepository dishRepository;
    private final DishMapper mapper;

    @Override
    public Mono<UUID> create(CreateDishDto dto) {
        return SecurityUtils.isPpAccessible(dto.getCompanyId(), dto.getPublicPointId())
                .then(dishRepository.save(map(dto)))
                .map(Dish::getId);
    }

    private Dish map(CreateDishDto dto) {
        Dish dish = mapper.fromDto(dto);
        dish.setId(UUID.randomUUID());
        dish.setDeleted(false);
        return dish;
    }

    @Override
    public Mono<Void> update(UUID id, UpdateDishDto updateDto) {
        return dishRepository.findById(id)
                .flatMap(this::isAccessible)
                .flatMap(dish -> {
                    mapper.update(dish, updateDto);
                    return dishRepository.save(dish);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    private Mono<Dish> isAccessible(Dish dish) {
        return SecurityUtils.isPpAccessible(dish.getCompanyId(), dish.getPublicPointId())
                .thenReturn(dish);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return dishRepository.findById(id)
                .flatMap(this::isAccessible)
                .flatMap(dish -> {
                    dish.setDeleted(true);
                    return dishRepository.save(dish);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    @Override
    public Mono<DishDto> findById(UUID id) {
        return findById(id, mapper::toDto);
    }

    private <T> Mono<T> findById(UUID id, Function<Dish, T> mapper) {
        return dishRepository.findById(id)
                .flatMap(dish -> SecurityUtils.isPpAccessible(dish.getCompanyId(), dish.getPublicPointId())
                        .thenReturn(dish))
                .switchIfEmpty(EntityNotFoundException.of(id))
                .map(mapper);
    }

    @Override
    public Mono<FullDetailsDishDto> findFullDetailsById(UUID id) {
        return findById(id, mapper::toFullDetailsDto);
    }

    @Override
    public Mono<Page<DishDto>> find(DishFilter filter, Pageable pageable) {
        Example<Dish> example = buildExample(filter);

        return SecurityUtils.isPpAccessible(filter.getCompanyId(), filter.getPublicPointId())
                .then(dishRepository.count(example))
                .zipWhen(count -> {
                    if (count > 0) {
                        return dishRepository.findAll(example, pageable.getSort())
                                .skip(pageable.getOffset())
                                .take(pageable.getPageSize())
                                .collectList();
                    } else {
                        return Mono.just(List.<Dish>of());
                    }
                })
                .map(data -> new PageImpl<>(data.getT2(), pageable, data.getT1())
                        .map(mapper::toDto)
                );
    }

    private Example<Dish> buildExample(DishFilter filter) {
        Dish example = new Dish();
        example.setCompanyId(filter.getCompanyId());
        example.setPublicPointId(filter.getPublicPointId());
        example.setName(filter.getNamePattern());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.startsWith().ignoreCase());
        if (filter.isWithDeleted()) {
            matcher = matcher.withIgnorePaths("deleted");
        } else {
            example.setDeleted(false);
        }

        return Example.of(example, matcher);
    }

    @Override
    public Mono<List<String>> findTags(UUID companyId, UUID publicPointId) {
        return SecurityUtils.isPpAccessible(companyId, publicPointId)
                .then(dishRepository.findTags(companyId, publicPointId));
    }

    @Override
    public Flux<DishNameDto> findNames(UUID companyId, UUID publicPointId) {
        return SecurityUtils.isPpAccessible(companyId, publicPointId)
                .thenMany(dishRepository.findActiveNames(companyId, publicPointId));
    }
}
