package com.acme.accountingsrv.plan.service.impl;

import com.acme.accountingsrv.plan.Plan;
import com.acme.accountingsrv.plan.PlanStatus;
import com.acme.accountingsrv.plan.dto.PlanDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountDto;
import com.acme.accountingsrv.plan.dto.PlanWithCountriesDto;
import com.acme.accountingsrv.plan.dto.PlanFilter;
import com.acme.accountingsrv.plan.dto.SavePlanDto;
import com.acme.accountingsrv.plan.exception.UpdateNotAllowedException;
import com.acme.accountingsrv.plan.mapper.PlanMapper;
import com.acme.accountingsrv.plan.repository.PublicPointPlanRepository;
import com.acme.accountingsrv.plan.repository.PlanRepository;
import com.acme.accountingsrv.plan.service.PlanService;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.commons.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.acme.commons.utils.StreamUtils.mapToList;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private static final Map<PlanStatus, List<PlanStatus>> ALLOWED_NEXT_STATUSES = Map.of(
            PlanStatus.INACTIVE, List.of(PlanStatus.ACTIVE, PlanStatus.STOPPED),
            PlanStatus.ACTIVE, List.of(PlanStatus.STOPPED)
    );

    private final PlanRepository planRepository;
    private final PublicPointPlanRepository publicPointPlanRepository;
    private final PlanMapper mapper;

    @Override
    @Transactional
    public Mono<UUID> create(SavePlanDto saveDto) {
        return planRepository.save(mapper.fromDto(saveDto))
                .flatMap(plan -> addCountries(plan, saveDto.getCountries()))
                .map(Plan::getId);
    }

    @Override
    @Transactional
    public Mono<Void> update(UUID id, SavePlanDto saveDto) {
        return planRepository.findById(id)
                .flatMap(plan -> update(plan, saveDto))
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    private Mono<Plan> update(Plan plan, SavePlanDto saveDto) {
        if (plan.getStatus() != PlanStatus.INACTIVE) {
            return Mono.error(UpdateNotAllowedException::new);
        }

        mapper.update(plan, saveDto);
        return planRepository.save(plan)
                .then(planRepository.clearCountries(plan.getId()))
                .then(addCountries(plan, saveDto.getCountries()));
    }

    @Override
    @Transactional
    public Mono<Void> changeStatus(UUID id, PlanStatus newStatus) {
        return planRepository.findById(id)
                .flatMap(plan -> isValidChange(plan, newStatus))
                .flatMap(plan -> {
                    plan.setStatus(newStatus);
                    return planRepository.save(plan);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    private Mono<Plan> isValidChange(Plan plan, PlanStatus newStatus) {
        List<PlanStatus> nextStatuses =
                ALLOWED_NEXT_STATUSES.getOrDefault(plan.getStatus(), emptyList());
        return nextStatuses.contains(newStatus) ? Mono.just(plan) : Mono.error(IllegalStatusChange::new);
    }

    private Mono<Plan> addCountries(Plan plan, Set<String> countries) {
        List<Mono<?>> monos = countries == null ? emptyList() :
                mapToList(countries, country ->
                        planRepository.addCountry(plan.getId(), country.toUpperCase()));
        return Mono.when(monos)
                .then(Mono.just(plan));
    }

    @Override
    public Mono<PlanWithCountriesDto> findById(UUID id) {
        return planRepository.findById(id)
                .zipWhen(plan -> planRepository.getCountries(plan.getId()))
                .map(data -> mapper.toDto(data.getT1(), data.getT2()))
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Mono<Page<PlanWithCountDto>> find(PlanFilter filter, Pageable pageable) {
        return planRepository.find(filter, pageable)
                .zipWhen(page -> Mono.zip(
                        getCountryMap(page.getContent()),
                        getCompanyCount(page.getContent())
                ))
                .map(data -> map(data.getT1(), data.getT2().getT1(), data.getT2().getT2()));
    }

    private Mono<Map<UUID, List<String>>> getCountryMap(List<Plan> plans) {
        List<UUID> ids = StreamUtils.mapToList(plans, Plan::getId);
        return planRepository.getCountries(ids);
    }

    private Mono<Map<UUID, Long>> getCompanyCount(List<Plan> plans) {
        List<UUID> ids = StreamUtils.mapToList(plans, Plan::getId);
        return publicPointPlanRepository.getPublicPointCount(ids);
    }

    private Page<PlanWithCountDto> map(Page<Plan> page,
                                       Map<UUID, List<String>> countryMap,
                                       Map<UUID, Long> countMap) {
        return page.map(plan -> mapper.toDtoWithCount(
                plan,
                countryMap.getOrDefault(plan.getId(), emptyList()),
                countMap.getOrDefault(plan.getId(), 0L)
        ));
    }

    @Override
    public Mono<List<PlanDto>> findActive(String country) {
        return planRepository.findActiveByCountry(country)
                .map(mapper::toDto)
                .collectList();
    }

}
