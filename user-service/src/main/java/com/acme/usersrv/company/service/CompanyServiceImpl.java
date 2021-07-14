package com.acme.usersrv.company.service;

import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.SaveOwnerDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.exception.IllegalStatusChange;
import com.acme.usersrv.company.mapper.CompanyMapper;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.mapper.UserMapper;
import com.acme.usersrv.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private static final Map<CompanyStatus, List<CompanyStatus>> ALLOWED_NEXT_STATUSES = Map.of(
            CompanyStatus.INACTIVE, List.of(CompanyStatus.ACTIVE, CompanyStatus.STOPPED),
            CompanyStatus.ACTIVE, List.of(CompanyStatus.SUSPENDED, CompanyStatus.STOPPED),
            CompanyStatus.SUSPENDED, List.of(CompanyStatus.ACTIVE)
    );

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Mono<UUID> register(RegisterCompanyDto registrationDto) {
        return duplicatesCheck(registrationDto)
                .map(this::mapFromDto)
                .flatMap(companyRepository::save)
                .flatMap(savedCompany ->
                        addOwner(savedCompany, registrationDto.getOwner())
                                .thenReturn(savedCompany.getId())
                );
    }

    private Company mapFromDto(RegisterCompanyDto registrationDto) {
        Company company = companyMapper.fromDto(registrationDto);
        company.setStatus(CompanyStatus.INACTIVE);
        return company;
    }

    private Mono<RegisterCompanyDto> duplicatesCheck(RegisterCompanyDto dto) {
        return companyRepository.existByMainParams(dto.getVatin(), dto.getRegNumber(), dto.getFullName())
                .filter(exits -> !exits)
                .map(exits -> dto)
                .switchIfEmpty(Mono.error(DuplicateCompanyException::new));
    }

    private Mono<UUID> addOwner(Company company, SaveOwnerDto ownerDto) {
        CreateUserDto saveUserDto = userMapper.convert(ownerDto);
        saveUserDto.setCompanyId(company.getId());
        saveUserDto.setRole(UserRole.COMPANY_OWNER);
        return userService.create(saveUserDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<CompanyDto>> find(CompanyFilter filter, Pageable pageable) {
        return companyRepository.find(filter, pageable)
                .map(page -> page.map(companyMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<CompanyDto>> findByJooq(CompanyFilter filter, Pageable pageable) {
        return companyRepository.findByJooq(filter, pageable)
                .map(page -> page.map(companyMapper::toDto));
    }

    @Override
    @Transactional
    public Mono<Void> changeStatus(UUID id, CompanyStatus newStatus) {
        return companyRepository.findById(id)
                .flatMap(company -> isValidChange(company, newStatus))
                .flatMap(company -> {
                    company.setStatus(newStatus);
                    return companyRepository.save(company);
                })
                .then();
    }

    private Mono<Company> isValidChange(Company company, CompanyStatus newStatus) {
        List<CompanyStatus> nextStatuses =
                ALLOWED_NEXT_STATUSES.getOrDefault(company.getStatus(), Collections.emptyList());
        return nextStatuses.contains(newStatus) ? Mono.just(company) : Mono.error(IllegalStatusChange::new);
    }
}
