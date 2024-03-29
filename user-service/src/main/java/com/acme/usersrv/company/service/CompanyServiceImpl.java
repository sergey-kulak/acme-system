package com.acme.usersrv.company.service;

import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.commons.security.SecurityUtils;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.CreateOwnerDto;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import com.acme.usersrv.company.event.CompanyRegisteredEvent;
import com.acme.usersrv.company.event.CompanyStatusChangedEvent;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.mapper.CompanyMapper;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.annotation.NewSpan;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private static final Map<CompanyStatus, List<CompanyStatus>> ALLOWED_NEXT_STATUSES = Map.of(
            CompanyStatus.INACTIVE, List.of(CompanyStatus.ACTIVE, CompanyStatus.STOPPED),
            CompanyStatus.ACTIVE, List.of(CompanyStatus.SUSPENDED, CompanyStatus.STOPPED),
            CompanyStatus.SUSPENDED, List.of(CompanyStatus.ACTIVE, CompanyStatus.STOPPED)
    );

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final CompanyMapper companyMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ReactiveTransactionManager txManager;

    @Override
    public Mono<UUID> register(RegisterCompanyDto registrationDto) {
        return duplicatesCheck(registrationDto)
                .map(this::mapFromDto)
                .flatMap(companyRepository::save)
                .flatMap(savedCompany ->
                        addOwner(savedCompany, registrationDto.getOwner())
                                .thenReturn(savedCompany.getId()))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(id -> notify(new CompanyRegisteredEvent(id)));
    }

    private Company mapFromDto(RegisterCompanyDto registrationDto) {
        Company company = companyMapper.fromDto(registrationDto);
        company.setStatus(CompanyStatus.INACTIVE);
        return company;
    }

    private Mono<RegisterCompanyDto> duplicatesCheck(RegisterCompanyDto dto) {
        return companyRepository.existByMainParams(dto.getVatin(), dto.getRegNumber(), dto.getFullName())
                .filter(exits -> !exits)
                .map(notExists -> dto)
                .switchIfEmpty(Mono.error(DuplicateCompanyException::new));
    }

    private Mono<UUID> addOwner(Company company, CreateOwnerDto ownerDto) {
        return userService.createCompanyOwner(company.getId(), ownerDto);
    }

    @Override
    public Mono<Page<FullDetailsCompanyDto>> find(CompanyFilter filter, Pageable pageable) {
        return companyRepository.find(filter, pageable)
                .map(page -> page.map(companyMapper::toFullDetailsDto));
    }

    @Override
    @NewSpan
    public Mono<Page<FullDetailsCompanyDto>> findByJooq(CompanyFilter filter, Pageable pageable) {
        return companyRepository.findByJooq(filter, pageable)
                .map(page -> page.map(companyMapper::toFullDetailsDto));
    }

    @Override
    public Mono<Void> changeStatus(UUID id, CompanyStatus newStatus) {
        return companyRepository.findById(id)
                .flatMap(company -> isValidChange(company, newStatus))
                .flatMap(company -> {
                    CompanyStatus fromStatus = company.getStatus();
                    company.setStatus(newStatus);
                    return companyRepository.save(company)
                            .thenReturn(CompanyStatusChangedEvent.builder()
                                    .companyId(id)
                                    .fromStatus(fromStatus)
                                    .toStatus(newStatus)
                                    .build());
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .as(TransactionalOperator.create(txManager)::transactional)
                .doOnSuccess(this::notify)
                .then();
    }

    private Mono<Company> isValidChange(Company company, CompanyStatus newStatus) {
        List<CompanyStatus> nextStatuses =
                ALLOWED_NEXT_STATUSES.getOrDefault(company.getStatus(), Collections.emptyList());
        return nextStatuses.contains(newStatus) ? Mono.just(company) : Mono.error(IllegalStatusChange::new);
    }

    private void notify(Object event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    @NewSpan
    public Mono<CompanyDto> findById(UUID id) {
        return findById(id, companyMapper::toDto);
    }

    private <T> Mono<T> findById(UUID id, Function<Company, T> mapper) {
        return SecurityUtils.hasCompanyAccess(id)
                .flatMap(companyRepository::findById)
                .map(mapper)
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    @NewSpan
    public Mono<FullDetailsCompanyDto> findFullDetailsById(UUID id) {
        return findById(id, companyMapper::toFullDetailsDto);
    }

    @Override
    @Transactional
    public Mono<Void> update(UUID id, UpdateCompanyDto dto) {
        return SecurityUtils.hasCompanyAccess(id)
                .flatMap(companyRepository::findById)
                .flatMap(company -> {
                    companyMapper.update(company, dto);
                    return companyRepository.save(company);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    @Override
    @NewSpan
    public Flux<CompanyDto> findNames(Collection<CompanyStatus> statuses) {
        return companyRepository.findNames(statuses);
    }
}
