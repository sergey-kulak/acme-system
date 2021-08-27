package com.acme.accountingsrv.plan.service.impl;

import com.acme.accountingsrv.plan.CompanyPlan;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.CompanyPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.exception.PlanAlreadyAssignedException;
import com.acme.accountingsrv.plan.mapper.CompanyPlanMapper;
import com.acme.accountingsrv.plan.mapper.PlanMapper;
import com.acme.accountingsrv.plan.repository.CompanyPlanRepository;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.accountingsrv.plan.service.CompanyPlanService;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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
                .then(companyPlanRepository.findByCompanyIdAndEndDateNull(companyId))
                .flatMap(curPlan -> stopPlan(curPlan, planId))
                .then(companyPlanRepository.save(createCompanyPlan(companyId, planId)))
                .map(CompanyPlan::getId);
    }

    private Mono<Void> stopPlan(CompanyPlan companyPlan, UUID planId) {
        if (Objects.equals(planId, companyPlan.getPlanId())) {
            return PlanAlreadyAssignedException.of(planId);
        }
        companyPlan.setEndDate(LocalDate.now().minusDays(1));
        return companyPlanRepository.save(companyPlan)
                .then();
    }

    private CompanyPlan createCompanyPlan(UUID companyId, UUID planId) {
        CompanyPlan cmpPlan = new CompanyPlan();
        cmpPlan.setCompanyId(companyId);
        cmpPlan.setPlanId(planId);

        return cmpPlan;
    }

    @Override
    public Mono<UUID> findActivePlan(UUID companyId) {
        return companyPlanRepository.findByCompanyIdAndEndDateNull(companyId)
                .map(CompanyPlan::getPlanId);
    }

    @Override
    public Mono<List<CompanyPlanDto>> getHistory(UUID companyId) {
        return companyPlanRepository.findByCompanyIdOrderByEndDate(companyId)
                .collectList()
                .zipWhen(this::getPlans)
                .map(data -> map(data.getT1(), data.getT2()));
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
}
