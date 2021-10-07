package com.acme.ppsrv.publicpoint.service.impl;

import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.commons.repository.RepoUtils;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.security.UserRole;
import com.acme.commons.utils.CollectionUtils;
import com.acme.commons.utils.StreamUtils;
import com.acme.ppsrv.plan.api.PublicPointPlanApi;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.dto.CreatePublicPointDto;
import com.acme.ppsrv.publicpoint.dto.FullDetailsPublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
import com.acme.ppsrv.publicpoint.event.PublicPointStatusChangedEvent;
import com.acme.ppsrv.publicpoint.exception.PlanNotAssignedException;
import com.acme.ppsrv.publicpoint.mapper.PublicPointMapper;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.publicpoint.service.PublicPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicPointServiceImpl implements PublicPointService {
    private static final Map<PublicPointStatus, List<PublicPointStatus>> ALLOWED_NEXT_STATUSES = Map.of(
            PublicPointStatus.INACTIVE, List.of(PublicPointStatus.ACTIVE, PublicPointStatus.STOPPED),
            PublicPointStatus.ACTIVE, List.of(PublicPointStatus.INACTIVE, PublicPointStatus.STOPPED)
    );

    private static final Map<PublicPointStatus, List<PublicPointStatus>> ACCT_ALLOWED_NEXT_STATUSES = Map.of(
            PublicPointStatus.ACTIVE, List.of(PublicPointStatus.SUSPENDED),
            PublicPointStatus.SUSPENDED, List.of(PublicPointStatus.ACTIVE)
    );

    private final PublicPointRepository ppRepository;
    private final PublicPointMapper mapper;
    private final PublicPointPlanApi ppPlanApi;
    private final ApplicationEventPublisher eventPublisher;
    private final ReactiveTransactionManager txManager;

    @Override
    @Transactional
    public Mono<UUID> create(CreatePublicPointDto saveDto) {
        return SecurityUtils.isCompanyAccessible(saveDto.getCompanyId())
                .then(ppRepository.save(mapper.fromDto(saveDto)))
                .flatMap(plan -> addLangs(plan, saveDto.getLangs()))
                .map(PublicPoint::getId);
    }

    private Mono<PublicPoint> addLangs(PublicPoint publicPoint, Collection<String> langs) {
        return RepoUtils.link(publicPoint, langs,
                lang -> ppRepository.addLang(publicPoint.getId(), lang.toLowerCase()));
    }

    @Override
    @Transactional
    public Mono<Void> update(UUID id, UpdatePublicPointDto saveDto) {
        return ppRepository.findById(id)
                .flatMap(pp -> SecurityUtils.isPpAccessible(pp.getCompanyId(), id)
                        .thenReturn(pp))
                .flatMap(plan -> update(plan, saveDto))
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    private Mono<PublicPoint> update(PublicPoint publicPoint, UpdatePublicPointDto saveDto) {
        mapper.update(publicPoint, saveDto);
        return ppRepository.save(publicPoint)
                .flatMap(pp -> ppRepository.clearLangs(publicPoint.getId())
                        .then(addLangs(publicPoint, saveDto.getLangs()))
                        .thenReturn(pp));
    }


    @Override
    public Mono<Void> changeStatus(UUID id, PublicPointStatus newStatus) {
        return ppRepository.findById(id)
                .flatMap(pp -> SecurityUtils.isPpAccessible(pp.getCompanyId(), id)
                        .thenReturn(pp))
                .flatMap(pp -> isValidChange(pp, newStatus))
                .flatMap(pp -> checkPlanForActive(pp, newStatus))
                .flatMap(pp -> {
                    PublicPointStatus fromStatus = pp.getStatus();
                    pp.setStatus(newStatus);
                    return ppRepository.save(pp)
                            .thenReturn(PublicPointStatusChangedEvent.builder()
                                    .publicPointId(id)
                                    .companyId(pp.getCompanyId())
                                    .fromStatus(fromStatus)
                                    .toStatus(newStatus)
                                    .build());
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(this::notify)
                .then();
    }

    private Mono<PublicPoint> checkPlanForActive(PublicPoint publicPoint,
                                                 PublicPointStatus newStatus) {
        return Mono.just(publicPoint)
                .filter(pp -> pp.getStatus() != PublicPointStatus.STOPPED && newStatus == PublicPointStatus.ACTIVE)
                .flatMap(pp -> ppPlanApi.findActivePlanId(pp.getId())
                        .switchIfEmpty(PlanNotAssignedException.of(pp.getName())))
                .thenReturn(publicPoint);
    }

    private Mono<PublicPoint> isValidChange(PublicPoint publicPoint, PublicPointStatus newStatus) {
        List<PublicPointStatus> nextStatuses =
                ALLOWED_NEXT_STATUSES.getOrDefault(publicPoint.getStatus(), List.of());

        return SecurityUtils.getCurrentUser()
                .filter(user -> user.hasAnyRole(UserRole.ADMIN))
                .map(user -> ACCT_ALLOWED_NEXT_STATUSES.getOrDefault(publicPoint.getStatus(), List.of()))
                .defaultIfEmpty(List.of())
                .map(acctNextStatuses -> CollectionUtils.union(acctNextStatuses, nextStatuses))
                .filter(allNextStatuses -> allNextStatuses.contains(newStatus))
                .switchIfEmpty(IllegalStatusChange.of())
                .thenReturn(publicPoint);
    }


    private void notify(Object event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public Mono<FullDetailsPublicPointDto> findFullDetailsById(UUID id) {
        return ppRepository.findById(id)
                .flatMap(pp -> SecurityUtils.isPpAccessible(pp.getCompanyId(), id)
                        .thenReturn(pp))
                .zipWhen(pp -> ppRepository.getLangs(pp.getId()))
                .map(data -> mapper.toDto(data.getT1(), data.getT2()))
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Mono<PublicPointDto> findById(UUID id) {
        return ppRepository.findDtoById(id)
                .flatMap(pp -> SecurityUtils.isPpAccessible(pp.getCompanyId(), id)
                        .thenReturn(pp))
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Flux<PublicPointDto> findNames(UUID companyId) {
        return ppRepository.findNotStoppedByCompanyId(companyId);
    }

    @Override
    public Mono<Page<FullDetailsPublicPointDto>> find(PublicPointFilter filter, Pageable pageable) {
        return SecurityUtils.isCompanyAccessible(filter.getCompanyId())
                .then(ppRepository.find(filter, pageable))
                .zipWhen(page -> getLangMap(page.getContent()))
                .map(data -> map(data.getT1(), data.getT2()));
    }

    private Mono<Map<UUID, List<String>>> getLangMap(List<PublicPoint> content) {
        List<UUID> ids = StreamUtils.mapToList(content, PublicPoint::getId);
        return ppRepository.getLangs(ids);
    }

    private Page<FullDetailsPublicPointDto> map(Page<PublicPoint> page,
                                                Map<UUID, List<String>> langMap) {
        return page.map(pp -> mapper.toDto(pp,
                langMap.getOrDefault(pp.getId(), List.of())));
    }
}
