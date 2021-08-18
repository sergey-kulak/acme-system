package com.acme.usersrv.company.service;


import com.acme.commons.exception.EntityNotFoundException;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.CreateOwnerDto;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.exception.IllegalStatusChange;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.test.RandomTestUtils;
import com.acme.usersrv.test.ServiceIntegrationTest;
import com.acme.usersrv.test.TestEntityHelper;
import com.acme.usersrv.test.TxStepVerifier;
import com.acme.usersrv.test.WithMockAdmin;
import com.acme.usersrv.test.WithMockCompanyOwner;
import com.acme.usersrv.test.WithMockPpManager;
import com.acme.usersrv.test.WithMockWaiter;
import com.acme.usersrv.user.User;
import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.acme.commons.utils.StreamUtils.mapToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ServiceIntegrationTest
public class CompanyServiceIntegrationTest {
    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    public void registrationValidation() {
        companyService.register(new RegisterCompanyDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    private RegisterCompanyDto createRegisterDto() {
        return RegisterCompanyDto.builder()
                .fullName(RandomTestUtils.randomString("Company"))
                .country("by")
                .city("city")
                .address("address")
                .email("email@company.com")
                .phone("+37291234567")
                .site("company.com")
                .vatin(RandomTestUtils.randomString("BY"))
                .owner(CreateOwnerDto.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .email(RandomTestUtils.randomEmail())
                        .password("qwe123")
                        .confirmPassword("qwe123")
                        .build())
                .build();
    }

    @Test
    public void successRegistration() {
        RegisterCompanyDto registerDto = createRegisterDto();

        companyService.register(registerDto)
                .flatMap(id -> Mono.zip(
                        companyRepository.findById(id),
                        userRepository.findCompanyOwners(id).collectList()
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    assertThat(data.getT1(), allOf(
                            hasProperty("fullName", is(registerDto.getFullName())),
                            hasProperty("country", is(registerDto.getCountry().toUpperCase())),
                            hasProperty("city", is(registerDto.getCity())),
                            hasProperty("address", is(registerDto.getAddress())),
                            hasProperty("email", is(registerDto.getEmail())),
                            hasProperty("phone", is(registerDto.getPhone())),
                            hasProperty("site", is(registerDto.getSite())),
                            hasProperty("status", is(CompanyStatus.INACTIVE)),
                            hasProperty("vatin", is(registerDto.getVatin().toUpperCase())),
                            hasProperty("regNumber", is(nullValue()))
                    ));
                    List<User> users = data.getT2();
                    assertThat(users, hasSize(1));
                    CreateOwnerDto ownerDto = registerDto.getOwner();
                    assertThat(users.get(0), allOf(
                            hasProperty("firstName", is(ownerDto.getFirstName())),
                            hasProperty("lastName", is(ownerDto.getLastName())),
                            hasProperty("email", is(ownerDto.getEmail())),
                            hasProperty("password", not(is(ownerDto.getPassword()))),
                            hasProperty("status", is(UserStatus.ACTIVE)),
                            hasProperty("role", is(UserRole.COMPANY_OWNER))
                    ));
                })
                .verifyComplete();
    }

    @Test
    public void companyDuplicateByVatin() {
        testCompanyDuplicate((dto, company) -> dto.setVatin(company.getVatin()));
    }

    @Test
    public void companyDuplicateByRegNumber() {
        testCompanyDuplicate((dto, company) -> dto.setRegNumber(company.getRegNumber()));
    }

    @Test
    public void companyDuplicateByFullName() {
        testCompanyDuplicate((dto, company) -> dto.setFullName(company.getFullName().toUpperCase()));
    }

    private void testCompanyDuplicate(BiConsumer<RegisterCompanyDto, Company> updater) {
        testEntityHelper.createCompany()
                .map(company -> {
                    RegisterCompanyDto registerDto = createRegisterDto();
                    updater.accept(registerDto, company);
                    return registerDto;
                })
                .flatMap(companyService::register)
                .as(TxStepVerifier::withRollback)
                .expectError(DuplicateCompanyException.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void findWithPagination() {
        findWithPagination((filter, pageable) -> companyService.find(filter, pageable));
    }

    private void findWithPagination(BiFunction<CompanyFilter, Pageable, Mono<Page<FullDetailsCompanyDto>>> finder) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("full_name")));
        testEntityHelper.createCompany()
                .zipWhen(company -> {
                            CompanyFilter filter = CompanyFilter.builder()
                                    .namePattern(company.getFullName().substring(0, 9))
                                    .status(Collections.singleton(company.getStatus()))
                                    .country(company.getCountry())
                                    .vatin(company.getVatin())
                                    .build();
                            return finder.apply(filter, pageable);
                        }
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    Page<FullDetailsCompanyDto> page = data.getT2();
                    assertThat(page.getTotalElements(), is(1L));
                    assertTrue(mapToList(page.getContent(), FullDetailsCompanyDto::getId).contains(company.getId()));
                })
                .verifyComplete();
    }

    @Test
    @WithMockAdmin
    public void findWithEmptyPagination() {
        findWithEmptyPagination((filter, pageable) -> companyService.find(filter, pageable));
    }

    private void findWithEmptyPagination(BiFunction<CompanyFilter, Pageable, Mono<Page<FullDetailsCompanyDto>>> finder) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("full_name")));
        CompanyFilter filter = CompanyFilter.builder()
                .vatin(RandomTestUtils.randomString("EU"))
                .build();
        finder.apply(filter, pageable)
                .as(TxStepVerifier::withRollback)
                .assertNext(page -> assertThat(page.getTotalElements(), is(0L)))
                .verifyComplete();
    }

    @Test
    @WithMockAdmin
    public void findByJooqWithPagination() {
        findWithPagination((filter, pageable) -> companyService.findByJooq(filter, pageable));
    }

    @Test
    @WithMockAdmin
    public void findByJooqWithEmptyPagination() {
        findWithEmptyPagination((filter, pageable) -> companyService.findByJooq(filter, pageable));
    }

    @Test
    @WithMockAdmin
    public void inactiveStatusToActive() {
        allowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.ACTIVE);
    }

    @Test
    @WithMockAdmin
    public void inactiveStatusToSuspended() {
        disallowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.SUSPENDED);
    }

    @Test
    @WithMockAdmin
    public void inactiveStatusToStopped() {
        allowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.STOPPED);
    }

    @Test
    @WithMockAdmin
    public void activeStatusToStopped() {
        allowedStatusChange(CompanyStatus.ACTIVE, CompanyStatus.STOPPED);
    }

    @Test
    @WithMockAdmin
    public void activeStatusToSuspended() {
        allowedStatusChange(CompanyStatus.ACTIVE, CompanyStatus.SUSPENDED);
    }

    @Test
    @WithMockAdmin
    public void suspendedStatusToActive() {
        allowedStatusChange(CompanyStatus.SUSPENDED, CompanyStatus.ACTIVE);
    }

    @Test
    @WithMockAdmin
    public void stoppedStatusToActive() {
        disallowedStatusChange(CompanyStatus.STOPPED, CompanyStatus.ACTIVE);
    }

    private void allowedStatusChange(CompanyStatus fromStatus, CompanyStatus toStatus) {
        testEntityHelper.createCompany(fromStatus)
                .flatMap(company ->
                        companyService.changeStatus(company.getId(), toStatus)
                                .then(companyRepository.findById(company.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(updatedCompany -> assertEquals(toStatus, updatedCompany.getStatus()))
                .verifyComplete();
    }

    private void disallowedStatusChange(CompanyStatus fromStatus, CompanyStatus toStatus) {
        testEntityHelper.createCompany(fromStatus)
                .flatMap(company -> companyService.changeStatus(company.getId(), toStatus))
                .as(TxStepVerifier::withRollback)
                .expectError(IllegalStatusChange.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void changeStatusValidation() {
        companyService.changeStatus(UUID.randomUUID(), null)
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @WithMockWaiter
    public void findById() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkWithCurrentUser)
                .zipWhen(company -> companyService.findById(company.getId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    CompanyDto dto = data.getT2();
                    assertThat(dto, allOf(
                            hasProperty("id", is(company.getId())),
                            hasProperty("fullName", is(company.getFullName())),
                            hasProperty("status", is(company.getStatus()))
                    ));
                })
                .verifyComplete();
    }

    @Test
    @WithMockWaiter
    public void findByIdOtherCompany() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkOtherCompanyWithCurrentUser)
                .zipWhen(company -> companyService.findById(company.getId()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void findByNotExistingId() {
        companyService.findById(UUID.randomUUID())
                .as(StepVerifier::create)
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void findByIdFullDetails() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkWithCurrentUser)
                .zipWhen(company -> companyService.findFullDetailsById(company.getId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    FullDetailsCompanyDto dto = data.getT2();
                    assertThat(dto, allOf(
                            hasProperty("id", is(company.getId())),
                            hasProperty("fullName", is(company.getFullName())),
                            hasProperty("status", is(company.getStatus())),
                            hasProperty("country", is(company.getCountry())),
                            hasProperty("city", is(company.getCity())),
                            hasProperty("address", is(company.getAddress())),
                            hasProperty("regNumber", is(company.getRegNumber())),
                            hasProperty("vatin", is(company.getVatin())),
                            hasProperty("email", is(company.getEmail())),
                            hasProperty("site", is(company.getSite())),
                            hasProperty("phone", is(company.getPhone()))
                    ));
                })
                .verifyComplete();
    }

    @Test
    @WithMockWaiter
    public void findByIdFullDetailsOtherCompany() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkOtherCompanyWithCurrentUser)
                .zipWhen(company -> companyService.findFullDetailsById(company.getId()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    public void update() {
        UpdateCompanyDto dto = createUpdateDto();
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkWithCurrentUser)
                .zipWhen(company ->
                        companyService.update(company.getId(), dto)
                                .then(companyRepository.findById(company.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    Company updCompany = data.getT2();
                    assertThat(updCompany, allOf(
                            hasProperty("id", is(company.getId())),
                            hasProperty("fullName", is(company.getFullName())),
                            hasProperty("status", is(company.getStatus())),
                            hasProperty("country", is(company.getCountry())),
                            hasProperty("city", is(dto.getCity())),
                            hasProperty("address", is(dto.getAddress())),
                            hasProperty("regNumber", is(company.getRegNumber())),
                            hasProperty("vatin", is(company.getVatin())),
                            hasProperty("email", is(dto.getEmail())),
                            hasProperty("site", is(dto.getSite())),
                            hasProperty("phone", is(dto.getPhone()))
                    ));
                })
                .verifyComplete();
    }

    private UpdateCompanyDto createUpdateDto() {
        return UpdateCompanyDto.builder()
                .address(RandomTestUtils.randomString("address"))
                .site(RandomTestUtils.randomString("site"))
                .email(RandomTestUtils.randomEmail())
                .phone(RandomTestUtils.randomString("phone"))
                .city("Brest")
                .build();
    }

    @Test
    @WithMockCompanyOwner
    public void updateOtherCompany() {
        UpdateCompanyDto dto = createUpdateDto();
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::linkOtherCompanyWithCurrentUser)
                .zipWhen(company -> companyService.update(company.getId(), dto))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockAdmin
    public void findActiveNames() {
        testEntityHelper.createCompany()
                .zipWhen(company -> companyService
                        .findNames(Collections.singleton(CompanyStatus.ACTIVE))
                        .collectList()
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    Optional<CompanyDto> companyDtoOp = data.getT2()
                            .stream()
                            .filter(dto -> Objects.equals(dto.getId(), company.getId()))
                            .findFirst();
                    assertTrue(companyDtoOp.isPresent());
                    assertThat(companyDtoOp.get(), allOf(
                            hasProperty("id", is(company.getId())),
                            hasProperty("fullName", is(company.getFullName())),
                            hasProperty("status", is(company.getStatus()))
                    ));

                })
                .verifyComplete();
    }
}