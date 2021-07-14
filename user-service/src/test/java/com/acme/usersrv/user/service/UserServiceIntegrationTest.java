package com.acme.usersrv.user.service;

import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.test.RandomTestUtils;
import com.acme.usersrv.test.ServiceIntegrationTest;
import com.acme.usersrv.test.TestEntityHelper;
import com.acme.usersrv.test.TxStepVerifier;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.exception.DuplicateUserException;
import com.acme.usersrv.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.UUID;

@ServiceIntegrationTest
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    public void creationValidation() {
        userService.create(new CreateUserDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    public void existsByEmail() {
        testEntityHelper.createCompany()
                .zipWhen(testEntityHelper::createCompanyOwner)
                .flatMap(data -> userService.existsByEmail(data.getT2().getEmail().toUpperCase()))
                .as(TxStepVerifier::withRollback)
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    public void notExistsByEmail() {
        userService.existsByEmail(RandomTestUtils.randomEmail())
                .as(TxStepVerifier::withRollback)
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
    }

    @Test
    public void createDuplicate() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::createCompanyOwner)
                .map(user -> {
                    CreateUserDto createDto = buildCreateDto(user.getCompanyId());
                    createDto.setEmail(user.getEmail());
                    return createDto;
                })
                .flatMap(userService::create)
                .as(TxStepVerifier::withRollback)
                .expectError(DuplicateUserException.class)
                .verify();
    }

    private CreateUserDto buildCreateDto(UUID companyId) {
        return CreateUserDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("ls@company.com")
                .password("qwe123")
                .confirmPassword("qwe123")
                .companyId(companyId)
                .role(UserRole.WAITER)
                .build();
    }
}