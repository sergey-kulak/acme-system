package com.acme.usersrv.user.controller;

import com.acme.usersrv.company.controller.CompanyController;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.exception.DuplicateUserException;
import com.acme.usersrv.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {UserController.class})
public class UserControllerTest {
    @Autowired
    WebTestClient webClient;

    @MockBean
    UserService userService;

    @Test
    public void createDuplicate() {
        when(userService.create(any(CreateUserDto.class)))
                .thenReturn(Mono.error(DuplicateUserException::new));

        webClient.post()
                .uri("/api/users")
                .bodyValue(new CreateUserDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String.class)
                .isEqualTo("User with specified email already exists");
    }
}