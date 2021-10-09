package com.acme.menusrv.dish.service;

import com.acme.menusrv.dish.Dish;
import com.acme.menusrv.dish.dto.CreateDishDto;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.DishFilter;
import com.acme.menusrv.dish.dto.DishNameDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import com.acme.menusrv.dish.repository.DishRepository;
import com.acme.menusrv.test.ServiceIntegrationTest;
import com.acme.menusrv.test.TestEntityHelper;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockChef;
import com.acme.testcommons.security.WithMockPpManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DishServiceIntegrationTest extends ServiceIntegrationTest {
    @Autowired
    DishService dishService;
    @Autowired
    DishRepository dishRepository;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    @WithMockChef
    void createValidation() {
        dishService.create(new CreateDishDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @WithMockPpManager
    void createDenied() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();

        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(TestSecurityUtils.linkOtherPpWithCurrentUser())
                .then(dishService.create(createDto(companyId, ppId)))
                .as(StepVerifier::create)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private CreateDishDto createDto(UUID companyId, UUID ppId) {
        return CreateDishDto.builder()
                .companyId(companyId)
                .publicPointId(ppId)
                .name("dish")
                .description("delicious")
                .primaryImage("primary")
                .images(List.of("secondary"))
                .composition("composition")
                .tags(List.of("tag1", "tag2"))
                .price(new BigDecimal("9.99"))
                .build();
    }

    @Test
    @WithMockPpManager
    void create() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        CreateDishDto dto = createDto(companyId, ppId);

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(dishService.create(dto))
                .flatMap(dishRepository::findById)
                .as(StepVerifier::create)
                .assertNext(dish -> assertThat(dish, allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("name", is(dto.getName())),
                        hasProperty("description", is(dto.getDescription())),
                        hasProperty("companyId", is(dto.getCompanyId())),
                        hasProperty("publicPointId", is(dto.getPublicPointId())),
                        hasProperty("composition", is(dto.getComposition())),
                        hasProperty("primaryImage", is(dto.getPrimaryImage())),
                        hasProperty("images", is(dto.getImages())),
                        hasProperty("tags", is(dto.getTags())),
                        hasProperty("deleted", is(false)),
                        hasProperty("price", is(dto.getPrice()))
                )))
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void findById() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(testEntityHelper.createDish(companyId, ppId))
                .zipWhen(dish -> dishService.findById(dish.getId()))
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Dish dish = data.getT1();
                    DishDto dto = data.getT2();

                    assertThat(dto, allOf(
                            hasProperty("id", is(dish.getId())),
                            hasProperty("name", is(dish.getName())),
                            hasProperty("description", is(dish.getDescription())),
                            hasProperty("composition", is(dish.getComposition())),
                            hasProperty("primaryImage", is(dish.getPrimaryImage())),
                            hasProperty("images", is(dish.getImages())),
                            hasProperty("tags", is(dish.getTags())),
                            hasProperty("deleted", is(dish.isDeleted())))
                    );
                })
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void delete() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(testEntityHelper.createDish(companyId, ppId))
                .flatMap(dish -> dishService.delete(dish.getId())
                        .then(dishService.findById(dish.getId())))
                .as(StepVerifier::create)
                .assertNext(dish -> assertTrue(dish.isDeleted()))
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void update() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        UpdateDishDto dto = updateDto();

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(testEntityHelper.createDish(companyId, ppId))
                .flatMap(dish -> dishService.update(dish.getId(), updateDto())
                        .then(dishRepository.findById(dish.getId())))
                .as(StepVerifier::create)
                .assertNext(dish -> assertThat(dish, allOf(
                        hasProperty("name", is(dto.getName())),
                        hasProperty("description", is(dto.getDescription())),
                        hasProperty("composition", is(dto.getComposition())),
                        hasProperty("primaryImage", is(dto.getPrimaryImage())),
                        hasProperty("images", is(dto.getImages())),
                        hasProperty("tags", is(dto.getTags())),
                        hasProperty("deleted", is(false)),
                        hasProperty("price", is(dto.getPrice()))
                )))
                .verifyComplete();
    }

    private UpdateDishDto updateDto() {
        return UpdateDishDto.builder()
                .name("dish")
                .description("delicious")
                .primaryImage("primary")
                .images(List.of("secondary"))
                .composition("composition")
                .tags(List.of("tag1", "tag2"))
                .price(new BigDecimal("8.5"))
                .build();
    }

    @Test
    @WithMockPpManager
    void find() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(createDeletedDish(companyId, ppId))
                .then(testEntityHelper.createDish(companyId, ppId))
                .zipWhen(dish -> {
                    DishFilter filter = DishFilter.builder()
                            .companyId(companyId)
                            .publicPointId(ppId)
                            .namePattern(dish.getName().toUpperCase())
                            .build();
                    return dishService.find(filter, pageable);
                })
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Dish dish = data.getT1();
                    Page<DishDto> page = data.getT2();

                    assertEquals(1, page.getTotalElements());

                    assertThat(page.getContent().get(0), allOf(
                            hasProperty("id", is(dish.getId())),
                            hasProperty("name", is(dish.getName())),
                            hasProperty("description", is(dish.getDescription())),
                            hasProperty("composition", is(dish.getComposition())),
                            hasProperty("primaryImage", is(dish.getPrimaryImage())),
                            hasProperty("images", is(dish.getImages())),
                            hasProperty("tags", is(dish.getTags())),
                            hasProperty("deleted", is(dish.isDeleted())))
                    );
                })
                .verifyComplete();
    }

    private Mono<Dish> createDeletedDish(UUID companyId, UUID ppId) {
        return testEntityHelper.createDish(companyId, ppId)
                .flatMap(dish -> {
                    dish.setDeleted(true);
                    return dishRepository.save(dish);
                });
    }

    @Test
    @WithMockPpManager
    void findWithDeleted() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(createDeletedDish(companyId, ppId))
                .zipWhen(deletedDish -> {
                    DishFilter filter = DishFilter.builder()
                            .companyId(companyId)
                            .publicPointId(ppId)
                            .withDeleted(true)
                            .namePattern(deletedDish.getName().toUpperCase())
                            .build();
                    return dishService.find(filter, pageable);
                })
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Dish deletedDish = data.getT1();
                    Page<DishDto> page = data.getT2();

                    assertEquals(1, page.getTotalElements());

                    assertThat(page.getContent().get(0),
                            hasProperty("id", is(deletedDish.getId()))
                    );
                })
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void findTags() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        Mono<Set<String>> tagsMono = Flux.fromStream(IntStream.rangeClosed(1, 3).boxed())
                .flatMap(i -> testEntityHelper.createDish(companyId, ppId))
                .map(Dish::getTags)
                .reduceWith(HashSet::new, (acc, tags) -> {
                    acc.addAll(tags);
                    return acc;
                });
        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(createDeletedDish(companyId, ppId))
                .then(tagsMono)
                .zipWhen(tags -> dishService.findTags(companyId, ppId))
                .as(StepVerifier::create)
                .assertNext(data -> assertTrue(data.getT1().containsAll(data.getT2())))
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void findNames() {
        UUID companyId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        Mono<Set<String>> tagsMono = Flux.fromStream(IntStream.rangeClosed(1, 3).boxed())
                .flatMap(i -> testEntityHelper.createDish(companyId, ppId))
                .map(Dish::getTags)
                .reduceWith(HashSet::new, (acc, tags) -> {
                    acc.addAll(tags);
                    return acc;
                });
        TestSecurityUtils.linkWithCurrentUser(companyId, ppId)
                .then(createDeletedDish(companyId, ppId))
                .then(testEntityHelper.createDish(companyId, ppId))
                .zipWhen(dish -> dishService.findNames(companyId, ppId).collectList())
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Dish dish = data.getT1();
                    List<DishNameDto> names = data.getT2();
                    assertEquals(1, names.size());

                    assertThat(names.get(0), allOf(
                            hasProperty("id", is(dish.getId())),
                            hasProperty("name", is(dish.getName()))
                    ));
                })
                .verifyComplete();
    }
}