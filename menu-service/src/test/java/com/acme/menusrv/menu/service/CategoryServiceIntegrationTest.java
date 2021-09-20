package com.acme.menusrv.menu.service;

import com.acme.menusrv.menu.Category;
import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.CreateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateMenuDto;
import com.acme.menusrv.menu.repository.CategoryRepository;
import com.acme.menusrv.test.ServiceIntegrationTest;
import com.acme.menusrv.test.TestEntityHelper;
import com.acme.commons.utils.StreamUtils;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockPpManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryServiceIntegrationTest extends ServiceIntegrationTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private TestEntityHelper testEntityHelper;

    @Test
    @WithMockPpManager
    void createValidation() {
        categoryService.create(new CreateCategoryDto())
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
                .then(categoryService.create(createDto(companyId, ppId)))
                .as(StepVerifier::create)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private CreateCategoryDto createDto(UUID cmpId, UUID ppId) {
        return CreateCategoryDto.builder()
                .companyId(cmpId)
                .publicPointId(ppId)
                .name("category")
                .days(Set.of(DayOfWeek.MONDAY))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
    }

    @Test
    @WithMockPpManager
    void create() {
        UUID cmpId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        UUID dishId = UUID.randomUUID();
        CreateCategoryDto dto = createDto(cmpId, ppId);
        dto.setDishIds(List.of(dishId));

        TestSecurityUtils.linkWithCurrentUser(cmpId)
                .then(TestSecurityUtils.linkPpWithCurrentUser(ppId))
                .then(categoryService.create(dto))
                .flatMap(categoryRepository::findById)
                .as(StepVerifier::create)
                .assertNext(category -> assertThat(category, allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("name", is(dto.getName())),
                        hasProperty("days", is(dto.getDays())),
                        hasProperty("companyId", is(dto.getCompanyId())),
                        hasProperty("publicPointId", is(dto.getPublicPointId())),
                        hasProperty("position", is(0)),
                        hasProperty("startTime", is(dto.getStartTime())),
                        hasProperty("endTime", is(dto.getEndTime())),
                        hasProperty("dishIds", is(dto.getDishIds()))
                )))
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void findById() {
        UUID cmpId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(cmpId)
                .then(TestSecurityUtils.linkPpWithCurrentUser(ppId))
                .then(testEntityHelper.createCategory(cmpId, ppId))
                .zipWhen(ctg -> categoryService.findById(ctg.getId()))
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Category ctg = data.getT1();
                    CategoryDto dto = data.getT2();

                    assertThat(ctg, allOf(
                            hasProperty("id", notNullValue()),
                            hasProperty("name", is(dto.getName())),
                            hasProperty("days", is(dto.getDays())),
                            hasProperty("companyId", is(dto.getCompanyId())),
                            hasProperty("publicPointId", is(dto.getPublicPointId())),
                            hasProperty("position", is(dto.getPosition())),
                            hasProperty("startTime", is(dto.getStartTime())),
                            hasProperty("endTime", is(dto.getEndTime())),
                            hasProperty("dishIds", is(dto.getDishIds()))));
                })
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void update() {
        UUID cmpId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();
        UpdateCategoryDto dto = updateDto();
        TestSecurityUtils.linkWithCurrentUser(cmpId)
                .then(TestSecurityUtils.linkPpWithCurrentUser(ppId))
                .then(testEntityHelper.createCategory(cmpId, ppId))
                .zipWhen(ctg -> categoryService.update(ctg.getId(), dto)
                        .then(categoryRepository.findById(ctg.getId())))
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Category oldCtg = data.getT1();
                    Category ctg = data.getT2();

                    assertThat(ctg, allOf(
                            hasProperty("name", is(dto.getName())),
                            hasProperty("days", is(dto.getDays())),
                            hasProperty("companyId", is(cmpId)),
                            hasProperty("publicPointId", is(ppId)),
                            hasProperty("position", is(oldCtg.getPosition())),
                            hasProperty("startTime", is(dto.getStartTime())),
                            hasProperty("endTime", is(dto.getEndTime())),
                            hasProperty("dishIds", is(dto.getDishIds()))));
                })
                .verifyComplete();
    }

    private UpdateCategoryDto updateDto() {
        return UpdateCategoryDto.builder()
                .name("category")
                .days(Set.of(DayOfWeek.MONDAY))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .dishIds(List.of(UUID.randomUUID()))
                .build();
    }

    @Test
    @WithMockPpManager
    void updateMenu() {
        UUID cmpId = UUID.randomUUID();
        UUID ppId = UUID.randomUUID();

        TestSecurityUtils.linkWithCurrentUser(cmpId)
                .then(TestSecurityUtils.linkPpWithCurrentUser(ppId))
                .then(Mono.zip(
                        testEntityHelper.createCategory(cmpId, ppId),
                        testEntityHelper.createCategory(cmpId, ppId),
                        testEntityHelper.createCategory(cmpId, ppId)
                ))
                .flatMap(data -> {
                    UpdateMenuDto dto = UpdateMenuDto.builder()
                            .companyId(cmpId)
                            .publicPointId(ppId)
                            .categoryIds(List.of(data.getT3().getId(), data.getT1().getId()))
                            .build();
                    return categoryService.update(dto)
                            .thenReturn(dto);
                })
                .zipWhen(r -> categoryRepository.findAll(cmpId, ppId).collectList())
                .as(StepVerifier::create)
                .assertNext(data -> {
                    UpdateMenuDto dto = data.getT1();
                    List<Category> categories = data.getT2();
                    categories.sort(Comparator.comparing(Category::getPosition));

                    assertEquals(2, categories.size());
                    assertEquals(dto.getCategoryIds(), StreamUtils.mapToList(categories, Category::getId));
                })
                .verifyComplete();
    }

}