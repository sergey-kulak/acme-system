package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PublicPointPlan;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PublicPointPlanDto;
import com.acme.accountingsrv.plan.exception.PlanAlreadyAssignedException;
import com.acme.accountingsrv.plan.exception.TableCountExceedLimitException;
import com.acme.accountingsrv.plan.repository.PublicPointPlanRepository;
import com.acme.accountingsrv.pubicpoint.api.PublicPointTableApi;
import com.acme.accountingsrv.test.ServiceIntegrationTest;
import com.acme.accountingsrv.test.TestEntityHelper;
import com.acme.commons.utils.StreamUtils;
import com.acme.testcommons.TxStepVerifier;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockAccountant;
import com.acme.testcommons.security.WithMockAdmin;
import com.acme.testcommons.security.WithMockCompanyOwner;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class PublicPointPlanServiceIntegrationTest extends ServiceIntegrationTest{
    @Autowired
    PublicPointPlanService publicPointPlanService;
    @Autowired
    PublicPointPlanRepository publicPointPlanRepository;
    @Autowired
    PublicPointTableApi ppTableApi;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    @WithMockAdmin
    void assign() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        mockTableCount(0);
        testEntityHelper.createPlan()
                .zipWhen(plan -> assignPlan(assignDto, plan.getId())
                        .flatMap(publicPointPlanRepository::findById))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    PublicPointPlan ppPlan = data.getT2();
                    assertThat(ppPlan, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("planId", is(plan.getId())),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));
                })
                .verifyComplete();
    }

    private AssignPlanDto createAssignDto(UUID companyId) {
        return AssignPlanDto.builder()
                .companyId(companyId)
                .publicPointId(UUID.randomUUID())
                .build();
    }

    private void mockTableCount(long count) {
        when(ppTableApi.countAll(any()))
                .thenReturn(Mono.just(count));
    }

    private Mono<UUID> assignPlan(AssignPlanDto dto, UUID planId) {
        return publicPointPlanService.assignPlan(dto.toBuilder().planId(planId).build());
    }

    private Mono<UUID> assignPlan(AssignPlanDto dto, UUID planId, int hoursAgo) {
        dto.setPlanId(planId);
        return testEntityHelper.assignPlan(dto)
                .doOnNext(ppPlan -> ppPlan.setStartDate(Instant.now().minusSeconds(hoursAgo * 3600L)))
                .flatMap(publicPointPlanRepository::save)
                .map(PublicPointPlan::getId);
    }

    @Test
    @WithMockAdmin
    void findActive() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(assignDto, plan.getId())
                        .thenReturn(plan))
                .zipWhen(plan -> publicPointPlanService.findActivePlan(assignDto.getPublicPointId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> assertEquals(data.getT1().getId(), data.getT2()))
                .verifyComplete();
    }

    @Test
    @WithMockAdmin
    void doubleActivePlan() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        mockTableCount(0);
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(assignDto, plan.getId())
                        .thenReturn(plan))
                .flatMap(plan -> assignPlan(assignDto, plan.getId())
                        .thenReturn(plan))
                .as(TxStepVerifier::withRollback)
                .expectError(PlanAlreadyAssignedException.class)
                .verify();
    }

    @Test
    @WithMockAccountant
    void assignPlanDenied() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        assignPlan(assignDto, UUID.randomUUID())
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockAccountant
    void assignPlanDeniedOtherCompany() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        TestSecurityUtils.linkWithCurrentUser(UUID.randomUUID())
                .then(assignPlan(assignDto, UUID.randomUUID()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void switchActivePlanCurrentWithinBillablePeriod() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        mockTableCount(0);
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(Mono.zip(
                        testEntityHelper.createPlan(),
                        testEntityHelper.createPlan()
                ))
                .zipWhen(planData ->
                        assignPlan(assignDto, planData.getT1().getId())
                                .then(assignPlan(assignDto, planData.getT2().getId()))
                                .then(publicPointPlanRepository
                                        .findByPublicPointIdOrderByEndDate(assignDto.getPublicPointId())
                                        .collectList())
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan2 = data.getT1().getT2();
                    List<PublicPointPlan> publicPointPlans = data.getT2();

                    assertEquals(1, publicPointPlans.size());
                    PublicPointPlan ppPlan = publicPointPlans.get(0);
                    assertThat(ppPlan, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("planId", is(plan2.getId())),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));

                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void switchActivePlan() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        mockTableCount(0);
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(Mono.zip(
                        testEntityHelper.createPlan(),
                        testEntityHelper.createPlan()
                ))
                .zipWhen(planData ->
                        assignPlan(assignDto, planData.getT1().getId(), 2)
                                .then(assignPlan(assignDto, planData.getT2().getId()))
                                .then(publicPointPlanRepository
                                        .findByPublicPointIdOrderByEndDate(assignDto.getPublicPointId())
                                        .collectList())
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan1 = data.getT1().getT1();
                    Plan plan2 = data.getT1().getT2();
                    List<PublicPointPlan> publicPointPlans = data.getT2();

                    assertEquals(2, publicPointPlans.size());
                    PublicPointPlan ppPlan1 = StreamUtils.filter(publicPointPlans,
                            ppPlan -> Objects.equals(ppPlan.getPlanId(), plan1.getId()))
                            .get(0);
                    assertThat(ppPlan1, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("planId", is(plan1.getId())),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(notNullValue()))
                    ));

                    PublicPointPlan ppPlan2 = StreamUtils.filter(publicPointPlans,
                            ppPlan -> Objects.equals(ppPlan.getPlanId(), plan2.getId()))
                            .get(0);
                    assertThat(ppPlan2, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("planId", is(plan2.getId())),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));

                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void getHistory() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(Mono.zip(
                        testEntityHelper.createPlan(),
                        testEntityHelper.createPlan()
                ))
                .zipWhen(planData ->
                        assignPlan(assignDto, planData.getT1().getId(), 1)
                                .then(assignPlan(assignDto, planData.getT2().getId()))
                                .then(publicPointPlanService.getHistory(assignDto.getPublicPointId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan1 = data.getT1().getT1();
                    Plan plan2 = data.getT1().getT2();
                    List<PublicPointPlanDto> companyPlans = data.getT2();

                    assertEquals(2, companyPlans.size());
                    PublicPointPlanDto ppPlan1 = companyPlans.get(0);
                    assertThat(ppPlan1, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("plan", is(planMatcher(plan1))),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(notNullValue()))
                    ));

                    PublicPointPlanDto ppPlan2 = companyPlans.get(1);
                    assertThat(ppPlan2, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("publicPointId", is(assignDto.getPublicPointId())),
                            hasProperty("plan", is(planMatcher(plan2))),
                            hasProperty("startDate", is(notNullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));

                })
                .verifyComplete();
    }

    private Matcher<PlanDto> planMatcher(Plan plan) {
        return allOf(
                hasProperty("name", is(plan.getName())),
                hasProperty("description", is(plan.getDescription())),
                hasProperty("status", is(plan.getStatus())),
                hasProperty("maxTableCount", is(plan.getMaxTableCount())),
                hasProperty("currency", is(plan.getCurrency().toUpperCase())),
                hasProperty("monthPrice", comparesEqualTo(plan.getMonthPrice())),
                hasProperty("upfrontDiscount6m", comparesEqualTo(plan.getUpfrontDiscount6m())),
                hasProperty("upfrontDiscount1y", comparesEqualTo(plan.getUpfrontDiscount1y()))
        );
    }

    @Test
    @WithMockAdmin
    void planStats() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(assignDto, plan.getId())
                        .thenReturn(plan))
                .flatMap(plan -> publicPointPlanService.findPlanStatistics(plan.getId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(stat -> {
                    assertEquals(1, stat.size());
                    assertEquals(1, stat.getOrDefault(companyId, 0L));
                })
                .verifyComplete();
    }

    @Test
    @WithMockAdmin
    void stopPlan() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(assignDto, plan.getId()))
                .flatMap(companyPlanId -> publicPointPlanService.stopActivePlan(assignDto.getPublicPointId())
                        .then(Mono.zip(
                                publicPointPlanRepository.findById(companyPlanId),
                                publicPointPlanService.findActivePlan(assignDto.getPublicPointId())
                                        .map(Optional::of)
                                        .defaultIfEmpty(Optional.empty())
                        ))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPointPlan publicPointPlan = data.getT1();
                    Optional<UUID> activePlanIdOp = data.getT2();

                    assertNotNull(publicPointPlan.getEndDate());
                    assertTrue(activePlanIdOp.isEmpty());
                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void stopPlanDenied() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        TestSecurityUtils.linkWithCurrentUser(UUID.randomUUID())
                .then(testEntityHelper.createPlan())
                .flatMap(plan -> {
                    assignDto.setPlanId(plan.getId());
                    return testEntityHelper.assignPlan(assignDto);
                })
                .flatMap(companyPlanId -> publicPointPlanService.stopActivePlan(companyId))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    void assignDeniedTableCountLimit() {
        UUID companyId = UUID.randomUUID();
        AssignPlanDto assignDto = createAssignDto(companyId);
        testEntityHelper.createPlan()
                .flatMap(plan -> {
                    mockTableCount(plan.getMaxTableCount() + 1);
                    return Mono.just(plan);
                })
                .zipWhen(plan -> assignPlan(assignDto, plan.getId()))
                .as(TxStepVerifier::withRollback)
                .expectError(TableCountExceedLimitException.class)
                .verify();
    }
}