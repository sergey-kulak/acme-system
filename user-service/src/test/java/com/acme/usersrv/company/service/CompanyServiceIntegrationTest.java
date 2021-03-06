package com.acme.usersrv.company.service;


import com.acme.usersrv.common.exception.EntityNotFoundException;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.SaveOwnerDto;
import com.acme.usersrv.company.dto.UpdateCompanyDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.exception.IllegalStatusChange;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.test.RandomTestUtils;
import com.acme.usersrv.test.ServiceIntegrationTest;
import com.acme.usersrv.test.TestEntityHelper;
import com.acme.usersrv.test.TxStepVerifier;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.acme.usersrv.common.utils.StreamUtils.mapToList;
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
                .fullName("Full company name")
                .country("by")
                .city("city")
                .address("address")
                .email("email@company.com")
                .phone("+37291234567")
                .site("company.com")
                .vatin("by1234567890")
                .owner(SaveOwnerDto.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .email("ls@company.com")
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
                    SaveOwnerDto ownerDto = registerDto.getOwner();
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
    public void findWithPagination() {
        findWithPagination((filter, pageable) -> companyService.find(filter, pageable));
    }

    private void findWithPagination(BiFunction<CompanyFilter, Pageable, Mono<Page<CompanyDto>>> finder) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("full_name")));
        testEntityHelper.createCompany()
                .zipWhen(company -> {
                            CompanyFilter filter = CompanyFilter.builder()
                                    .namePattern(company.getFullName().substring(0, 9))
                                    .statuses(Collections.singleton(company.getStatus()))
                                    .country(company.getCountry())
                                    .vatin(company.getVatin())
                                    .build();
                            return finder.apply(filter, pageable);
                        }
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    Company company = data.getT1();
                    Page<CompanyDto> page = data.getT2();
                    assertThat(page.getTotalElements(), is(1L));
                    assertTrue(mapToList(page.getContent(), CompanyDto::getId).contains(company.getId()));
                })
                .verifyComplete();
    }

    @Test
    public void findWithEmptyPagination() {
        findWithEmptyPagination((filter, pageable) -> companyService.find(filter, pageable));
    }

    private void findWithEmptyPagination(BiFunction<CompanyFilter, Pageable, Mono<Page<CompanyDto>>> finder) {
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
    public void findByJooqWithPagination() {
        findWithPagination((filter, pageable) -> companyService.findByJooq(filter, pageable));
    }

    @Test
    public void findByJooqWithEmptyPagination() {
        findWithEmptyPagination((filter, pageable) -> companyService.findByJooq(filter, pageable));
    }

    @Test
    public void inactiveStatusToActive() {
        allowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.ACTIVE);
    }

    @Test
    public void inactiveStatusToSuspended() {
        disallowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.SUSPENDED);
    }

    @Test
    public void inactiveStatusToStopped() {
        allowedStatusChange(CompanyStatus.INACTIVE, CompanyStatus.STOPPED);
    }

    @Test
    public void activeStatusToStopped() {
        allowedStatusChange(CompanyStatus.ACTIVE, CompanyStatus.STOPPED);
    }

    @Test
    public void activeStatusToSuspended() {
        allowedStatusChange(CompanyStatus.ACTIVE, CompanyStatus.SUSPENDED);
    }

    @Test
    public void suspendedStatusToActive() {
        allowedStatusChange(CompanyStatus.SUSPENDED, CompanyStatus.ACTIVE);
    }

    @Test
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
    public void changeStatusValidation() {
        companyService.changeStatus(UUID.randomUUID(), null)
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    public void findById() {
        testEntityHelper.createCompany()
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
    public void findByNotExistingId() {
        companyService.findById(UUID.randomUUID())
                .as(StepVerifier::create)
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    public void findByIdFullDetails() {
        testEntityHelper.createCompany()
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
    public void update() {
        UpdateCompanyDto dto = UpdateCompanyDto.builder()
                .address(RandomTestUtils.randomString("address"))
                .site(RandomTestUtils.randomString("site"))
                .email(RandomTestUtils.randomEmail())
                .phone(RandomTestUtils.randomString("phone"))
                .city("Brest")
                .build();
        testEntityHelper.createCompany()
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
}