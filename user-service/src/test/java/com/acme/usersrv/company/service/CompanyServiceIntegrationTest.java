package com.acme.usersrv.company.service;


import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.dto.SaveOwnerDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.test.ServiceIntegrationTest;
import com.acme.usersrv.test.TestEntityHelper;
import com.acme.usersrv.test.TxStepVerifier;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

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
}