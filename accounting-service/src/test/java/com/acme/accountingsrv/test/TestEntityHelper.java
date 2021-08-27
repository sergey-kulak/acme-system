package com.acme.accountingsrv.test;

import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.repository.CompanyPlanRepository;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.testcommons.RandomTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private CompanyPlanRepository companyPlanRepository;

    public Mono<Plan> createPlan() {
        return createPlan(PlanStatus.ACTIVE, "BY");
    }

    private Plan buildPlan(PlanStatus status) {
        Plan plan = new Plan();
        plan.setName(RandomTestUtils.randomString("Plan"));
        plan.setDescription(RandomTestUtils.randomString("Descr"));
        plan.setStatus(status);
        plan.setCurrency("USD");
        plan.setMaxTableCount(RandomUtils.nextInt(10, 50));
        plan.setMonthPrice(new BigDecimal("59.99"));
        plan.setUpfrontDiscount6m(new BigDecimal("10.06"));
        plan.setUpfrontDiscount1y(new BigDecimal("20.12"));

        return plan;
    }

    public Mono<Plan> createPlan(PlanStatus status, String country) {
        return planRepository.save(buildPlan(status))
                .flatMap(savedPlan -> planRepository.addCountry(savedPlan.getId(), country)
                        .then(Mono.just(savedPlan))
                );
    }

    public Mono<Plan> createGlobalPlan(PlanStatus status) {
        return planRepository.save(buildPlan(status));
    }

    public Mono<CompanyPlan> assignPlan(UUID companyId, UUID planId) {
        CompanyPlan cmpPlan = new CompanyPlan();
        cmpPlan.setCompanyId(companyId);
        cmpPlan.setPlanId(planId);

        return companyPlanRepository.save(cmpPlan);
    }
}
