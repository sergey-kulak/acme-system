package com.acme.usersrv.company.controller;

import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {CompanyController.class})
public class CompanyControllerTest {
    @Autowired
    WebTestClient webClient;

    @MockBean
    CompanyService companyService;

    @Test
    public void registration() {
        UUID id = UUID.randomUUID();
        when(companyService.register(any(RegisterCompanyDto.class)))
                .thenReturn(Mono.just(id));

        webClient.post()
                .uri("/api/companies")
                .bodyValue(new RegisterCompanyDto())
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.id").isEqualTo(id.toString());
    }

    @Test
    public void duplicateCompany() {
        when(companyService.register(any(RegisterCompanyDto.class)))
                .thenReturn(Mono.error(DuplicateCompanyException::new));

        webClient.post()
                .uri("/api/companies")
                .bodyValue(new RegisterCompanyDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String.class)
                .isEqualTo("Company with specified vatin, reg number or full name already registered");
    }

}