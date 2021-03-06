package com.acme.usersrv.user.service;

import com.acme.usersrv.common.exception.EntityNotFoundException;
import com.acme.usersrv.test.RandomTestUtils;
import com.acme.usersrv.test.ServiceIntegrationTest;
import com.acme.usersrv.test.TestEntityHelper;
import com.acme.usersrv.test.TxStepVerifier;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UpdateUserDto;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.exception.DuplicateUserException;
import com.acme.usersrv.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.UUID;

import static com.acme.usersrv.common.utils.StreamUtils.mapToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ServiceIntegrationTest
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityHelper testEntityHelper;
    @Autowired
    PasswordEncoder passwordEncoder;

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

    @Test
    public void findById() {
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::createCompanyOwner)
                .zipWhen(user -> userService.findById(user.getId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    User user = data.getT1();
                    UserDto dto = data.getT2();
                    assertThat(dto, allOf(
                            hasProperty("id", is(user.getId())),
                            hasProperty("firstName", is(user.getFirstName())),
                            hasProperty("lastName", is(user.getLastName())),
                            hasProperty("email", is(user.getEmail())),
                            hasProperty("phone", is(user.getPhone())),
                            hasProperty("companyId", is(user.getCompanyId())),
                            hasProperty("role", is(user.getRole())),
                            hasProperty("status", is(user.getStatus()))
                    ));
                })
                .verifyComplete();
    }

    @Test
    public void findByNotExistingId() {
        userService.findById(UUID.randomUUID())
                .as(StepVerifier::create)
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    private UpdateUserDto buildUpdateDto() {
        return UpdateUserDto.builder()
                .firstName(RandomTestUtils.randomString("nFirstName"))
                .lastName(RandomTestUtils.randomString("nLastName"))
                .phone(RandomTestUtils.randomString("nPhone"))
                .role(UserRole.WAITER)
                .build();
    }

    @Test
    public void updateWithPassword() {
        String password = RandomStringUtils.randomAlphanumeric(8);
        UpdateUserDto dto = buildUpdateDto();
        dto.setPassword(password);
        dto.setConfirmPassword(password);
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::createCompanyOwner)
                .zipWhen(user -> userService.update(user.getId(), dto)
                        .then(userRepository.findById(user.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    User user = data.getT1();
                    User updatedUser = data.getT2();
                    assertThat(updatedUser, allOf(
                            hasProperty("id", is(user.getId())),
                            hasProperty("firstName", is(dto.getFirstName())),
                            hasProperty("lastName", is(dto.getLastName())),
                            hasProperty("email", is(user.getEmail())),
                            hasProperty("phone", is(dto.getPhone())),
                            hasProperty("companyId", is(user.getCompanyId())),
                            hasProperty("role", is(dto.getRole())),
                            hasProperty("status", is(user.getStatus()))
                    ));
                    assertTrue(passwordEncoder.matches(dto.getPassword(), updatedUser.getPassword()));
                })
                .verifyComplete();
    }

    @Test
    public void updateWithoutPassword() {
        UpdateUserDto dto = buildUpdateDto();
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::createCompanyOwner)
                .zipWhen(user -> userService.update(user.getId(), dto)
                        .then(userRepository.findById(user.getId()))
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    User user = data.getT1();
                    User updatedUser = data.getT2();
                    assertThat(updatedUser, allOf(
                            hasProperty("id", is(user.getId())),
                            hasProperty("firstName", is(dto.getFirstName())),
                            hasProperty("lastName", is(dto.getLastName())),
                            hasProperty("email", is(user.getEmail())),
                            hasProperty("phone", is(dto.getPhone())),
                            hasProperty("companyId", is(user.getCompanyId())),
                            hasProperty("role", is(dto.getRole())),
                            hasProperty("status", is(user.getStatus())),
                            hasProperty("password", is(user.getPassword()))
                    ));
                })
                .verifyComplete();
    }

    @Test
    public void updateValidation() {
        UpdateUserDto dto = buildUpdateDto();
        dto.setPassword("qwe");
        assertThrows(ConstraintViolationException.class, () -> userService.update(UUID.randomUUID(), dto));
    }

    @Test
    public void findWithPagination() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("last_name")));
        testEntityHelper.createCompany()
                .flatMap(testEntityHelper::createCompanyOwner)
                .zipWhen(user -> {
                            UserFilter filter = UserFilter.builder()
                                    .email(user.getEmail().substring(0, 9))
                                    .statuses(Collections.singleton(user.getStatus()))
                                    .companyId(user.getCompanyId())
                                    .roles(Collections.singleton(user.getRole()))
                                    .build();
                            return userService.find(filter, pageable);
                        }
                )
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    User user = data.getT1();
                    Page<UserDto> page = data.getT2();
                    assertThat(page.getTotalElements(), is(1L));
                    assertTrue(mapToList(page.getContent(), UserDto::getId).contains(user.getId()));
                })
                .verifyComplete();
    }
}