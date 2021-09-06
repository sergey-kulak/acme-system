package com.acme.accountingsrv.company.event;

import com.acme.accountingsrv.company.CompanyStatus;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.service.PublicPointPlanService;
import com.acme.commons.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyEventService {
    private final PublicPointPlanService publicPointPlanService;

//    public Mono<Void> onEvent(CompanyRegisteredEvent event) {
//        log.info("received event: {}", event);
//
//        AssignPlanDto dto = new AssignPlanDto(event.getCompanyId(), event.getPlanId());
//        return publicPointPlanService.assignPlan(dto)
//                .onErrorResume(Throwable.class, e -> {
//                    log.error("Error during plan assignment", e);
//                    return Mono.empty();
//                })
//                .contextWrite(SecurityUtils.withBgAdmin())
//                .then();
//    }
//
//    public Mono<Void> onEvent(CompanyStatusChangedEvent event) {
//        log.info("received event: {}", event);
//
//        if (event.getToStatus() == CompanyStatus.STOPPED) {
//            return publicPointPlanService.stopActivePlan(event.getCompanyId())
//                    .onErrorResume(Throwable.class, e -> {
//                        log.error("Error during company status change processing", e);
//                        return Mono.empty();
//                    })
//                    .contextWrite(SecurityUtils.withBgAdmin())
//                    .then();
//        } else {
//            return Mono.empty().then();
//        }
//    }
}
