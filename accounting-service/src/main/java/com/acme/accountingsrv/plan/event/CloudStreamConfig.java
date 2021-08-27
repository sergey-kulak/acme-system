package com.acme.accountingsrv.plan.event;

import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.service.CompanyPlanService;
import com.acme.commons.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
public class CloudStreamConfig {
    @Autowired
    private CompanyPlanService companyPlanService;

    @Bean
    public Function<Flux<CompanyRegisteredEvent>, Flux<Void>> companyRegisteredConsumer() {
        return flux -> flux.flatMap(event -> {
            AssignPlanDto dto = new AssignPlanDto(event.getCompanyId(), event.getPlanId());
            return companyPlanService.assignPlan(dto)
                    .onErrorResume(Throwable.class, e -> {
                        log.error("Error during plan assignment", e);
                        return Mono.empty();
                    })
                    .contextWrite(SecurityUtils.withBgAdmin())
                    .then();
        });
    }

}
