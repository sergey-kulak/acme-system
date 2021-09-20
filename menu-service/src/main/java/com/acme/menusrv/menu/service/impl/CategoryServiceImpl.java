package com.acme.menusrv.menu.service.impl;

import com.acme.menusrv.menu.Category;
import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.CreateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateMenuDto;
import com.acme.menusrv.menu.mapper.CategoryMapper;
import com.acme.menusrv.menu.repository.CategoryRepository;
import com.acme.menusrv.menu.service.CategoryService;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<UUID> create(CreateCategoryDto dto) {
        return SecurityUtils.isPpAccessible(dto.getCompanyId(), dto.getPublicPointId())
                .then(map(dto))
                .flatMap(categoryRepository::save)
                .map(Category::getId);
    }

    private Mono<Category> map(CreateCategoryDto dto) {
        Category category = categoryMapper.fromDto(dto, UUID.randomUUID());

        return categoryRepository.countAll(dto.getCompanyId(), dto.getPublicPointId())
                .map(Long::intValue)
                .doOnNext(category::setPosition)
                .thenReturn(category);
    }

    @Override
    public Mono<Void> update(UUID id, UpdateCategoryDto dto) {
        return categoryRepository.findById(id)
                .flatMap(this::checkAccess)
                .flatMap(ct -> {
                    categoryMapper.update(ct, dto);
                    return categoryRepository.save(ct);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    @Override
    public Mono<Void> update(UpdateMenuDto dto) {
        return SecurityUtils.isPpAccessible(dto.getCompanyId(), dto.getPublicPointId())
                .then(updatePositions(dto))
                .then(delete(dto));
    }

    private Mono<Void> updatePositions(UpdateMenuDto dto) {
        List<Mono<Void>> updates = IntStream
                .range(0, dto.getCategoryIds().size())
                .boxed()
                .map(pos -> updatePosition(pos, dto))
                .collect(Collectors.toList());

        return Mono.when(updates);
    }

    private Mono<Void> updatePosition(int position, UpdateMenuDto dto) {
        UUID cmpId = dto.getCompanyId();
        UUID ppId = dto.getPublicPointId();
        List<UUID> categoryIds = dto.getCategoryIds();

        return categoryRepository.updatePosition(categoryIds.get(position), cmpId, ppId, position);
    }

    private Mono<Void> delete(UpdateMenuDto dto) {
        UUID cmpId = dto.getCompanyId();
        UUID ppId = dto.getPublicPointId();
        List<UUID> categoryIds = dto.getCategoryIds();
        return categoryIds.isEmpty() ? categoryRepository.deleteAll(cmpId, ppId) :
                categoryRepository.deleteNotIn(cmpId, ppId, categoryIds);
    }

    private Mono<Category> checkAccess(Category ct) {
        return SecurityUtils.isPpAccessible(ct.getCompanyId(), ct.getPublicPointId())
                .thenReturn(ct);
    }

    @Override
    public Mono<CategoryDto> findById(UUID id) {
        return categoryRepository.findById(id)
                .flatMap(this::checkAccess)
                .map(categoryMapper::toDto)
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Flux<CategoryDto> findAll(UUID companyId, UUID publicPointId) {
        return SecurityUtils.isPpAccessible(companyId, publicPointId)
                .thenMany(categoryRepository.findAll(companyId, publicPointId))
                .map(categoryMapper::toDto);
    }
}
