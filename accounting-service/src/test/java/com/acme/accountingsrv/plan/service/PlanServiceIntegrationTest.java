package com.acme.accountingsrv.plan.service;


import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.accountingsrv.plan.exception.UpdateNotAllowedException;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.accountingsrv.test.ServiceIntegrationTest;
import com.acme.accountingsrv.test.TestEntityHelper;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.testcommons.RandomTestUtils;
import com.acme.testcommons.TxStepVerifier;
import com.acme.testcommons.security.WithMockAccountant;
import com.acme.testcommons.security.WithMockAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.acme.commons.utils.StreamUtils.mapToList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ServiceIntegrationTest
class PlanServiceIntegrationTest {
    @Autowired
    PlanService planService;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    CompanyPlanService companyPlanService;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    @WithMockAdmin
    public void createValidation() {
        planService.create(new SavePlanDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    private SavePlanDto createSaveDto() {
        return SavePlanDto.builder()
                .name(RandomTestUtils.randomString("Plan"))
                .description(RandomTestUtils.randomString("Descr"))
                .maxTableCount(100)
                .currency("usd")
                .monthPrice(new BigDecimal("99.99"))
                .upfrontDiscount6m(new BigDecimal("5.5"))
                .upfrontDiscount1y(new BigDecimal("12"))
                .countries(new HashSet<>(Arrays.asList("ru", "by")))
                .build();
    }

    @Test
    @WithMockAccountant
    void create() {
        SavePlanDto createDto = createSaveDto();
        planService.create(createDto)
                .flatMap(id -> Mono.zip(
                        planRepository.findById(id),
                        planRepository.getCountries(id)
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    assertThat(plan, allOf(
                            hasProperty("name", is(createDto.getName())),
                            hasProperty("description", is(createDto.getDescription())),
                            hasProperty("status", is(PlanStatus.INACTIVE)),
                            hasProperty("maxTableCount", is(createDto.getMaxTableCount())),
                            hasProperty("currency", is(createDto.getCurrency().toUpperCase())),
                            hasProperty("monthPrice", comparesEqualTo(createDto.getMonthPrice())),
                            hasProperty("upfrontDiscount6m", comparesEqualTo(createDto.getUpfrontDiscount6m())),
                            hasProperty("upfrontDiscount1y", comparesEqualTo(createDto.getUpfrontDiscount1y()))
                    ));
                    List<String> countries = data.getT2();
                    assertEquals(mapToList(createDto.getCountries(), String::toUpperCase), countries);
                })
                .verifyComplete();
    }

    @Test
    @WithMockAccountant
    void findById() {
        testEntityHelper.createPlan()
                .zipWhen(plan -> Mono.zip(
                        planService.findById(plan.getId()),
                        planRepository.getCountries(plan.getId())
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    PlanWithCountriesDto dto = data.getT2().getT1();
                    assertThat(dto, allOf(
                            hasProperty("name", is(plan.getName())),
                            hasProperty("description", is(plan.getDescription())),
                            hasProperty("status", is(plan.getStatus())),
                            hasProperty("maxTableCount", is(plan.getMaxTableCount())),
                            hasProperty("currency", is(plan.getCurrency().toUpperCase())),
                            hasProperty("monthPrice", comparesEqualTo(plan.getMonthPrice())),
                            hasProperty("upfrontDiscount6m", comparesEqualTo(plan.getUpfrontDiscount6m())),
                            hasProperty("upfrontDiscount1y", comparesEqualTo(plan.getUpfrontDiscount1y()))
                    ));
                    assertEquals(data.getT2().getT2(), dto.getCountries());
                })
                .verifyComplete();
    }

    @Test
    @WithMockAccountant
    void update() {
        SavePlanDto updateDto = createSaveDto();
        testEntityHelper.createPlan(PlanStatus.INACTIVE, "BY")
                .flatMap(plan -> planService.update(plan.getId(), updateDto)
                        .then(planService.findById(plan.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(dto -> {
                    assertThat(dto, allOf(
                            hasProperty("name", is(updateDto.getName())),
                            hasProperty("description", is(updateDto.getDescription())),
                            hasProperty("status", is(PlanStatus.INACTIVE)),
                            hasProperty("maxTableCount", is(updateDto.getMaxTableCount())),
                            hasProperty("currency", is(updateDto.getCurrency().toUpperCase())),
                            hasProperty("monthPrice", comparesEqualTo(updateDto.getMonthPrice())),
                            hasProperty("upfrontDiscount6m", comparesEqualTo(updateDto.getUpfrontDiscount6m())),
                            hasProperty("upfrontDiscount1y", comparesEqualTo(updateDto.getUpfrontDiscount1y()))
                    ));
                    assertEquals(mapToList(updateDto.getCountries(), String::toUpperCase), dto.getCountries());
                })
                .verifyComplete();
    }

    @Test
    @WithMockAccountant
    void updateNotFound() {
        SavePlanDto updateDto = createSaveDto();
        planService.update(UUID.randomUUID(), updateDto)
                .as(StepVerifier::create)
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    @WithMockAccountant
    void updateActive() {
        SavePlanDto updateDto = createSaveDto();
        testEntityHelper.createPlan()
                .flatMap(plan -> planService.update(plan.getId(), updateDto))
                .as(TxStepVerifier::withRollback)
                .expectError(UpdateNotAllowedException.class)
                .verify();
    }

    private void allowedStatusChange(PlanStatus fromStatus, PlanStatus toStatus) {
        testEntityHelper.createPlan(fromStatus, "BY")
                .flatMap(company ->
                        planService.changeStatus(company.getId(), toStatus)
                                .then(planRepository.findById(company.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(updatedPlan -> assertEquals(toStatus, updatedPlan.getStatus()))
                .verifyComplete();
    }

    private void disallowedStatusChange(PlanStatus fromStatus, PlanStatus toStatus) {
        testEntityHelper.createPlan(fromStatus, "BY")
                .flatMap(company -> planService.changeStatus(company.getId(), toStatus))
                .as(TxStepVerifier::withRollback)
                .expectError(IllegalStatusChange.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void inactiveStatusToActive() {
        allowedStatusChange(PlanStatus.INACTIVE, PlanStatus.ACTIVE);
    }

    @Test
    @WithMockAdmin
    public void activeStatusToStopped() {
        allowedStatusChange(PlanStatus.ACTIVE, PlanStatus.STOPPED);
    }

    @Test
    @WithMockAdmin
    public void inactiveStatusToStopped() {
        allowedStatusChange(PlanStatus.INACTIVE, PlanStatus.STOPPED);
    }

    @Test
    @WithMockAdmin
    public void stoppedStatusToActive() {
        disallowedStatusChange(PlanStatus.STOPPED, PlanStatus.ACTIVE);
    }

    private PlanFilter createFilter(Plan plan) {
        return PlanFilter.builder()
                .namePattern(plan.getName().toUpperCase())
                .status(singletonList(plan.getStatus()))
                .tableCount(plan.getMaxTableCount())
                .country("by")
                .build();
    }

    private Mono<UUID> assignPlan(UUID companyId, UUID planId) {
        return testEntityHelper.assignPlan(companyId, planId)
                .map(CompanyPlan::getId);
    }

    @Test
    @WithMockAccountant
    void findNotOnlyGlobalWithCompanyCount() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(UUID.randomUUID(), plan.getId())
                        .thenReturn(plan)
                )
                .flatMap(plan -> Mono.zip(
                        planService.findById(plan.getId()),
                        planService.find(createFilter(plan), pageable)
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PlanWithCountriesDto plan = data.getT1();
                    Page<PlanWithCountDto> page = data.getT2();
                    assertEquals(1, page.getTotalElements());
                    PlanWithCountDto foundItem = page.getContent().get(0);
                    assertEquals(foundItem.getId(), plan.getId());
                    assertEquals(plan.getCountries(), foundItem.getCountries());
                    assertEquals(plan.getCountries(), foundItem.getCountries());
                    assertEquals(1, foundItem.getCompanyCount());
                })
                .verifyComplete();
    }

    @Test
    @WithMockAccountant
    void findOnlyGlobalEmpty() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        testEntityHelper.createPlan()
                .flatMap(plan -> {
                    PlanFilter filter = createFilter(plan);
                    filter.setOnlyGlobal(true);
                    return planService.find(filter, pageable);
                })
                .as(TxStepVerifier::withRollback)
                .assertNext(page -> assertEquals(0, page.getTotalElements()))
                .verifyComplete();
    }

    @Test
    @WithMockAccountant
    void findOnlyGlobal() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        testEntityHelper.createGlobalPlan(PlanStatus.INACTIVE)
                .zipWhen(plan -> {
                    PlanFilter filter = createFilter(plan);
                    filter.setOnlyGlobal(true);
                    return planService.find(filter, pageable);
                })
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    Page<PlanWithCountDto> page = data.getT2();
                    assertEquals(1, page.getTotalElements());
                    assertTrue(mapToList(page.getContent(), PlanWithCountDto::getId)
                            .contains(plan.getId()));
                })
                .verifyComplete();
    }
    @Test
    @WithMockAccountant
    void findByCompany() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        UUID companyId = UUID.randomUUID();
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(companyId, plan.getId())
                        .thenReturn(plan)
                )
                .zipWhen(plan -> {
                    PlanFilter filter = createFilter(plan);
                    filter.setCompanyId(companyId);
                    return planService.find(filter, pageable);
                })
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    Page<PlanWithCountDto> page = data.getT2();
                    assertEquals(1, page.getTotalElements());
                    assertTrue(mapToList(page.getContent(), PlanWithCountDto::getId)
                            .contains(plan.getId()));
                })
                .verifyComplete();
    }

    @Test
    void findActive() {
        String country = "TC";
        Mono.zip(testEntityHelper.createGlobalPlan(PlanStatus.ACTIVE),
                testEntityHelper.createGlobalPlan(PlanStatus.STOPPED),
                testEntityHelper.createPlan(PlanStatus.ACTIVE, country),
                testEntityHelper.createPlan(PlanStatus.INACTIVE, country))
                .zipWhen(data -> planService.findActive(country))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    UUID activeGlobal = data.getT1().getT1().getId();
                    UUID notActiveGlobal = data.getT1().getT2().getId();
                    UUID activeCountry = data.getT1().getT3().getId();
                    UUID notActiveCountry = data.getT1().getT4().getId();

                    List<UUID> planIds = mapToList(data.getT2(), PlanDto::getId);
                    assertTrue(planIds.contains(activeGlobal));
                    assertTrue(planIds.contains(activeCountry));

                    assertFalse(planIds.contains(notActiveGlobal));
                    assertFalse(planIds.contains(notActiveCountry));
                })
                .verifyComplete();
    }


}