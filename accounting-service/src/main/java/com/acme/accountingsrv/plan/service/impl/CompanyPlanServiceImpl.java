package com.acme.accountingsrv.plan.service.impl;

import com.acme.accountingsrv.common.Constants;
import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.exception.PlanAlreadyAssignedException;
import com.acme.accountingsrv.plan.mapper.CompanyPlanMapper;
import com.acme.accountingsrv.plan.mapper.PlanMapper;
import com.acme.accountingsrv.plan.repository.CompanyIdOnly;
import com.acme.accountingsrv.plan.repository.CompanyPlanRepository;
import com.acme.accountingsrv.plan.repository.PlanIdOnly;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.accountingsrv.plan.service.CompanyPlanService;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyPlanServiceImpl implements CompanyPlanService {
    private final CompanyPlanRepository companyPlanRepository;
    private final PlanRepository planRepository;
    private final CompanyPlanMapper companyPlanMapper;
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public Mono<UUID> assignPlan(AssignPlanDto dto) {
        UUID companyId = dto.getCompanyId();
        UUID planId = dto.getPlanId();
        log.info("assign {} plan to {} company", planId, companyId);

        return SecurityUtils.isCompanyAccessible(companyId)
                .then(companyPlanRepository.findActiveCompanyPlan(companyId))
                .flatMap(curPlan -> processPlan(curPlan, companyId, planId))
                .switchIfEmpty(companyPlanRepository.save(createCompanyPlan(companyId, planId)))
                .map(CompanyPlan::getId);
    }

    private Mono<CompanyPlan> processPlan(CompanyPlan curCmpPlan, UUID companyId, UUID planId) {
        if (Objects.equals(planId, curCmpPlan.getPlanId())) {
            return PlanAlreadyAssignedException.of(planId);
        }
        boolean useSamePlan = Duration.between(curCmpPlan.getStartDate(), Instant.now())
                .getSeconds() < Constants.BILLABLE_PERIOD;
        if (useSamePlan) {
            curCmpPlan.setPlanId(planId);
        } else {
            curCmpPlan.setEndDate(Instant.now());
        }
        return companyPlanRepository.save(curCmpPlan)
                .flatMap(savedCurCmpPlan -> useSamePlan ? Mono.just(curCmpPlan) :
                        companyPlanRepository.save(createCompanyPlan(companyId, planId)));
    }

    private CompanyPlan createCompanyPlan(UUID companyId, UUID planId) {
        CompanyPlan cmpPlan = new CompanyPlan();
        cmpPlan.setCompanyId(companyId);
        cmpPlan.setPlanId(planId);
        cmpPlan.setStartDate(Instant.now());

        return cmpPlan;
    }

    @Override
    public Mono<UUID> findActivePlan(UUID companyId) {
        return companyPlanRepository.findActivePlanId(companyId)
                .map(PlanIdOnly::getPlanId);
    }

    @Override
    public Mono<List<CompanyPlanDto>> getHistory(UUID companyId) {
        return companyPlanRepository.findByCompanyIdOrderByEndDate(companyId)
                .collectList()
                .zipWhen(this::getPlans)
                .map(data -> map(data.getT1(), data.getT2()));
    }

    @Override
    public Flux<UUID> findCompanyIdsWithPlan(UUID planId) {
        return companyPlanRepository.findByPlanId(planId)
                .map(CompanyIdOnly::getCompanyId);
    }

    private List<CompanyPlanDto> map(List<CompanyPlan> cmpPlans, List<Plan> plans) {
        Map<UUID, PlanDto> planMap = StreamUtils.mapToMap(plans, Plan::getId, planMapper::toDto);
        return StreamUtils.mapToList(cmpPlans,
                cmpPlan -> companyPlanMapper.toDto(cmpPlan, planMap.get(cmpPlan.getPlanId())));
    }

    private Mono<List<Plan>> getPlans(List<CompanyPlan> companyPlans) {
        Set<UUID> ids = StreamUtils.mapToSet(companyPlans, CompanyPlan::getPlanId);
        return planRepository.findAllById(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Void> stopActivePlan(UUID companyId) {
        return SecurityUtils.isCompanyAccessible(companyId)
                .then(companyPlanRepository.findActiveCompanyPlan(companyId))
                .flatMap(companyPlan -> {
                    companyPlan.setEndDate(Instant.now());
                    return companyPlanRepository.save(companyPlan);
                })
                .then();
    }
}
