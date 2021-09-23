package com.acme.ppsrv.publicpoint.service.impl;

import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.security.SecurityUtils;
import com.acme.commons.utils.CollectionUtils;
import com.acme.commons.utils.StreamUtils;
import com.acme.ppsrv.plan.api.PublicPointPlanApi;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTablesDto;
import com.acme.ppsrv.publicpoint.exception.PlanTableLimitExceededException;
import com.acme.ppsrv.publicpoint.mapper.PublicPointTableMapper;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.ppsrv.publicpoint.service.PublicPointTableService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicPointTableServiceImpl implements PublicPointTableService {
    private static final int CODE_LENGTH = 50;

    private final PublicPointTableRepository ppTableRepository;
    private final PublicPointRepository ppRepository;
    private final PublicPointPlanApi ppPlanApi;
    private final PublicPointTableMapper ppTableMapper;

    @Override
    public Flux<PublicPointTableDto> findAll(UUID ppId) {
        return checkAccess(ppId)
                .thenMany(ppTableRepository.findByPublicPointIdOrderByName(ppId, PublicPointTableDto.class));
    }

    private Mono<Void> checkAccess(UUID ppId) {
        return ppRepository.findById(ppId)
                .flatMap(pp -> SecurityUtils.isPpAccessible(pp.getCompanyId(), ppId));
    }

    private Mono<Void> checkPlanLimit(SavePpTablesDto saveDto) {
        UUID ppId = saveDto.getPublicPointId();
        long onlyAdded = saveDto.getChanged() == null ? 0 :
                StreamUtils.filter(saveDto.getChanged(), item -> item.getId() == null).size();
        long added = onlyAdded - CollectionUtils.size(saveDto.getDeleted());

        return ppPlanApi.findActivePlan(ppId)
                .zipWhen(plan -> ppTableRepository.countByPublicPointId(ppId))
                .filter(data -> data.getT2() + added <= data.getT1().getMaxTableCount())
                .switchIfEmpty(PlanTableLimitExceededException.of())
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> save(SavePpTablesDto saveDto) {
        return checkAccess(saveDto.getPublicPointId())
                .then(checkPlanLimit(saveDto))
                .then(internalSave(saveDto));
    }

    private Mono<Void> internalSave(SavePpTablesDto saveDto) {
        UUID ppId = saveDto.getPublicPointId();
        List<Mono<Void>> modifications = CollectionUtils.isEmpty(saveDto.getChanged()) ? List.of() :
                saveDto.getChanged()
                        .stream()
                        .map(dto -> internalSave(dto, ppId))
                        .collect(Collectors.toList());

        Mono<Void> deletion = CollectionUtils.isEmpty(saveDto.getDeleted()) ? Mono.empty()
                : ppTableRepository.deleteByPublicPointId(saveDto.getDeleted(), ppId);

        return Mono.when(modifications)
                .then(deletion);
    }

    private Mono<Void> internalSave(SavePpTableDto dto, UUID ppId) {
        if (dto.getId() == null) {
            PublicPointTable table = ppTableMapper.fromDto(dto, ppId);
            table.setCode(generateCode());
            return ppTableRepository.save(table)
                    .then();
        } else {
            return ppTableRepository.update(dto.getId(), ppId,
                    dto.getName(), dto.getDescription(), dto.getSeatCount());
        }
    }

    private String generateCode() {
        return RandomStringUtils.randomAlphanumeric(CODE_LENGTH);
    }

    @Override
    public Mono<Long> countAll(UUID ppId) {
        return checkAccess(ppId)
                .then(ppTableRepository.countByPublicPointId(ppId));
    }

    @Override
    public Mono<String> getCode(UUID id) {
        return ppTableRepository.findById(id)
                .flatMap(table -> checkAccess(table.getPublicPointId()).thenReturn(table))
                .map(PublicPointTable::getCode)
                .switchIfEmpty(EntityNotFoundException.of(id));
    }
}
