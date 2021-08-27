package com.acme.accountingsrv.plan.service;

import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.exception.PlanAlreadyAssignedException;
import com.acme.accountingsrv.plan.repository.CompanyPlanRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ServiceIntegrationTest
class CompanyPlanServiceIntegrationTest {
    @Autowired
    CompanyPlanService companyPlanService;
    @Autowired
    CompanyPlanRepository companyPlanRepository;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    @WithMockAdmin
    void assign() {
        UUID companyId = UUID.randomUUID();
        testEntityHelper.createPlan()
                .zipWhen(plan -> assignPlan(companyId, plan.getId())
                        .flatMap(companyPlanRepository::findById))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan = data.getT1();
                    CompanyPlan cmpPlan = data.getT2();
                    assertThat(cmpPlan, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("planId", is(plan.getId())),
                            hasProperty("startDate", is(nullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));
                })
                .verifyComplete();
    }

    private Mono<UUID> assignPlan(UUID companyId, UUID planId) {
        return companyPlanService.assignPlan(new AssignPlanDto(companyId, planId));
    }

    @Test
    @WithMockAdmin
    void findActive() {
        UUID companyId = UUID.randomUUID();
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(companyId, plan.getId())
                        .thenReturn(plan))
                .zipWhen(plan -> companyPlanService.findActivePlan(companyId))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> assertEquals(data.getT1().getId(), data.getT2()))
                .verifyComplete();
    }

    @Test
    @WithMockAdmin
    void doubleActivePlan() {
        UUID companyId = UUID.randomUUID();
        testEntityHelper.createPlan()
                .flatMap(plan -> assignPlan(companyId, plan.getId())
                        .thenReturn(plan))
                .flatMap(plan -> assignPlan(companyId, plan.getId())
                        .thenReturn(plan))
                .as(TxStepVerifier::withRollback)
                .expectError(PlanAlreadyAssignedException.class)
                .verify();
    }

    @Test
    @WithMockAccountant
    void assignPlanDenied() {
        assignPlan(UUID.randomUUID(), UUID.randomUUID())
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockAccountant
    void assignPlanDeniedOtherCompany() {
        TestSecurityUtils.linkWithCurrentUser(UUID.randomUUID())
                .then(assignPlan(UUID.randomUUID(), UUID.randomUUID()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void switchActivePlan() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(Mono.zip(
                        testEntityHelper.createPlan(),
                        testEntityHelper.createPlan()
                ))
                .zipWhen(planData ->
                        assignPlan(companyId, planData.getT1().getId())
                                .then(assignPlan(companyId, planData.getT2().getId()))
                                .then(companyPlanRepository.findByCompanyIdOrderByEndDate(companyId).collectList())
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan1 = data.getT1().getT1();
                    Plan plan2 = data.getT1().getT2();
                    List<CompanyPlan> companyPlans = data.getT2();

                    assertEquals(2, companyPlans.size());
                    CompanyPlan cmpPlan1 = StreamUtils.filter(companyPlans,
                            cmpPlan -> Objects.equals(cmpPlan.getPlanId(), plan1.getId()))
                            .get(0);
                    assertThat(cmpPlan1, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("planId", is(plan1.getId())),
                            hasProperty("startDate", is(nullValue())),
                            hasProperty("endDate", is(LocalDate.now().minusDays(1)))
                    ));

                    CompanyPlan cmpPlan2 = StreamUtils.filter(companyPlans,
                            cmpPlan -> Objects.equals(cmpPlan.getPlanId(), plan2.getId()))
                            .get(0);
                    assertThat(cmpPlan2, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("planId", is(plan2.getId())),
                            hasProperty("startDate", is(nullValue())),
                            hasProperty("endDate", is(nullValue()))
                    ));

                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void getHistory() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(Mono.zip(
                        testEntityHelper.createPlan(),
                        testEntityHelper.createPlan()
                ))
                .zipWhen(planData ->
                        assignPlan(companyId, planData.getT1().getId())
                                .then(assignPlan(companyId, planData.getT2().getId()))
                                .then(companyPlanService.getHistory(companyId))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Plan plan1 = data.getT1().getT1();
                    Plan plan2 = data.getT1().getT2();
                    List<CompanyPlanDto> companyPlans = data.getT2();

                    assertEquals(2, companyPlans.size());
                    CompanyPlanDto cmpPlan1 = companyPlans.get(0);
                    assertThat(cmpPlan1, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("plan", is(planMatcher(plan1))),
                            hasProperty("startDate", is(nullValue())),
                            hasProperty("endDate", is(LocalDate.now().minusDays(1)))
                    ));

                    CompanyPlanDto cmpPlan2 = companyPlans.get(1);
                    assertThat(cmpPlan2, allOf(
                            hasProperty("companyId", is(companyId)),
                            hasProperty("plan", is(planMatcher(plan2))),
                            hasProperty("startDate", is(nullValue())),
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

}