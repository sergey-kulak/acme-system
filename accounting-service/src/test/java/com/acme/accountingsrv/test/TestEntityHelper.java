package com.acme.accountingsrv.test;

import com.acme.accountingsrv.plan.PublicPointPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.repository.PublicPointPlanRepository;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.testcommons.RandomTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PublicPointPlanRepository publicPointPlanRepository;

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
                        .thenReturn(savedPlan)
                );
    }

    public Mono<Plan> createGlobalPlan(PlanStatus status) {
        return planRepository.save(buildPlan(status));
    }

    public Mono<PublicPointPlan> assignPlan(AssignPlanDto dto) {
        PublicPointPlan ppPlan = new PublicPointPlan();
        ppPlan.setCompanyId(dto.getCompanyId());
        ppPlan.setPlanId(dto.getPlanId());
        ppPlan.setPublicPointId(dto.getPublicPointId());
        ppPlan.setStartDate(Instant.now());

        return publicPointPlanRepository.save(ppPlan);
    }
}
