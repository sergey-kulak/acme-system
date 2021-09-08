package com.acme.accountingsrv.plan.service.impl;

import com.acme.accountingsrv.common.Constants;
import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PublicPointPlan;
import com.acme.accountingsrv.plan.dto.AssignPlanDto;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PublicPointPlanDto;
import com.acme.accountingsrv.plan.exception.PlanAlreadyAssignedException;
import com.acme.accountingsrv.plan.exception.TableCountExceedLimitException;
import com.acme.accountingsrv.plan.mapper.CompanyPlanMapper;
import com.acme.accountingsrv.plan.mapper.PlanMapper;
import com.acme.accountingsrv.plan.repository.CompanyPpCount;
import com.acme.accountingsrv.plan.repository.PlanIdOnly;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.accountingsrv.plan.repository.PublicPointPlanRepository;
import com.acme.accountingsrv.plan.service.PublicPointPlanService;
import com.acme.accountingsrv.pubicpoint.api.PublicPointTableApi;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class PublicPointPlanServiceImpl implements PublicPointPlanService {
    private final PublicPointPlanRepository publicPointPlanRepository;
    private final PlanRepository planRepository;
    private final CompanyPlanMapper companyPlanMapper;
    private final PlanMapper planMapper;
    private final PublicPointTableApi ppTableApi;

    @Override
    @Transactional
    public Mono<UUID> assignPlan(AssignPlanDto dto) {
        UUID companyId = dto.getCompanyId();
        UUID publicPointId = dto.getPublicPointId();
        UUID planId = dto.getPlanId();
        log.info("assign {} plan to {} pp from {} company", planId, publicPointId, companyId);

        return SecurityUtils.isCompanyAccessible(companyId)
                .then(publicPointPlanRepository.findActivePlan(publicPointId))
                .flatMap(curPlan -> processPlan(curPlan, dto))
                .switchIfEmpty(saveNewPlan(dto))
                .map(PublicPointPlan::getId);
    }

    private Mono<PublicPointPlan> saveNewPlan(AssignPlanDto dto) {
        return checkTableCount(dto.getPublicPointId(), dto.getPlanId())
                .then(publicPointPlanRepository.save(createCompanyPlan(dto)));
    }

    private Mono<Void> checkTableCount(UUID publicPointId, UUID planId) {
        return Mono.zip(
                ppTableApi.countAll(publicPointId),
                planRepository.findById(planId))
                .filter(data -> data.getT1() <= data.getT2().getMaxTableCount())
                .switchIfEmpty(TableCountExceedLimitException.of())
                .then();
    }

    private Mono<PublicPointPlan> processPlan(PublicPointPlan curPlan, AssignPlanDto dto) {
        UUID planId = dto.getPlanId();
        if (Objects.equals(planId, curPlan.getPlanId())) {
            return PlanAlreadyAssignedException.of(planId);
        }
        boolean useSamePlan = Duration.between(curPlan.getStartDate(), Instant.now())
                .getSeconds() < Constants.BILLABLE_PERIOD;
        if (useSamePlan) {
            curPlan.setPlanId(planId);
        } else {
            curPlan.setEndDate(Instant.now());
        }
        return checkTableCount(dto.getPublicPointId(), dto.getPlanId())
                .then(publicPointPlanRepository.save(curPlan))
                .flatMap(savedCurCmpPlan -> useSamePlan ? Mono.just(curPlan) :
                        publicPointPlanRepository.save(createCompanyPlan(dto)));
    }

    private PublicPointPlan createCompanyPlan(AssignPlanDto dto) {
        PublicPointPlan cmpPlan = new PublicPointPlan();
        cmpPlan.setCompanyId(dto.getCompanyId());
        cmpPlan.setPlanId(dto.getPlanId());
        cmpPlan.setPublicPointId(dto.getPublicPointId());
        cmpPlan.setStartDate(Instant.now());

        return cmpPlan;
    }

    @Override
    public Mono<UUID> findActivePlan(UUID publicPointId) {
        return publicPointPlanRepository.findActivePlanId(publicPointId)
                .map(PlanIdOnly::getPlanId);
    }

    @Override
    public Mono<List<PublicPointPlanDto>> getHistory(UUID publicPointId) {
        return publicPointPlanRepository.findByPublicPointIdOrderByEndDate(publicPointId)
                .collectList()
                .flatMap(ppPlans -> SecurityUtils.isCompanyAccessible(ppPlans.get(0).getCompanyId())
                        .thenReturn(ppPlans))
                .zipWhen(this::getPlans)
                .map(data -> map(data.getT1(), data.getT2()));
    }

    @Override
    public Mono<Map<UUID, Long>> findPlanStatistics(UUID planId) {
        return publicPointPlanRepository.findPlanStatistics(planId)
                .collectList()
                .map(items -> StreamUtils.mapToMap(items,
                        CompanyPpCount::getCompanyId, CompanyPpCount::getPpCount));
    }

    private List<PublicPointPlanDto> map(List<PublicPointPlan> cmpPlans, List<Plan> plans) {
        Map<UUID, PlanDto> planMap = StreamUtils.mapToMap(plans, Plan::getId, planMapper::toDto);
        return StreamUtils.mapToList(cmpPlans,
                cmpPlan -> companyPlanMapper.toDto(cmpPlan, planMap.get(cmpPlan.getPlanId())));
    }

    private Mono<List<Plan>> getPlans(List<PublicPointPlan> publicPointPlans) {
        Set<UUID> ids = StreamUtils.mapToSet(publicPointPlans, PublicPointPlan::getPlanId);
        return planRepository.findAllById(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Void> stopActivePlan(UUID publicPointId) {
        return publicPointPlanRepository.findActivePlan(publicPointId)
                .flatMap(pp -> SecurityUtils.isCompanyAccessible(pp.getCompanyId())
                        .thenReturn(pp))
                .flatMap(companyPlan -> {
                    companyPlan.setEndDate(Instant.now());
                    return publicPointPlanRepository.save(companyPlan);
                })
                .switchIfEmpty(EntityNotFoundException.of(publicPointId))
                .then();
    }
}
