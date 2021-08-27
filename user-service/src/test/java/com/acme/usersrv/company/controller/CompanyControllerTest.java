package com.acme.usersrv.company.controller;

import com.acme.commons.exception.EntityNotFoundException;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.dto.CompanyDto;
import com.acme.usersrv.company.dto.CompanyFilter;
import com.acme.usersrv.company.dto.CompanyStatusDto;
import com.acme.usersrv.company.dto.FullDetailsCompanyDto;
import com.acme.usersrv.company.dto.RegisterCompanyDto;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.usersrv.company.service.CompanyService;
import com.acme.testcommons.ControllerTest;
import com.acme.testcommons.RandomTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ControllerTest(CompanyController.class)
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

    @Test
    public void findWithEmptyPagination() {
        when(companyService.findByJooq(any(CompanyFilter.class), any(Pageable.class)))
                .thenReturn(Mono.just(Page.empty()));

        webClient.get()
                .uri("/api/companies")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.totalElements").isEqualTo(0);
    }

    @Test
    public void changeStatusAllowed() {
        UUID id = UUID.randomUUID();
        CompanyStatusDto statusDto = new CompanyStatusDto(CompanyStatus.ACTIVE);
        when(companyService.changeStatus(id, statusDto.getStatus()))
                .thenReturn(Mono.empty());

        webClient.put()
                .uri("/api/companies/{id}/status", id)
                .bodyValue(statusDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);
    }

    @Test
    public void changeStatusDisallowed() {
        UUID id = UUID.randomUUID();
        CompanyStatusDto statusDto = new CompanyStatusDto(CompanyStatus.ACTIVE);
        when(companyService.changeStatus(id, statusDto.getStatus()))
                .thenReturn(Mono.error(IllegalStatusChange::new));

        webClient.put()
                .uri("/api/companies/{id}/status", id)
                .bodyValue(statusDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void findByNotExistingId() {
        UUID id = UUID.randomUUID();
        when(companyService.findById(id))
                .thenReturn(Mono.error(new EntityNotFoundException(id)));

        webClient.get()
                .uri("/api/companies/{id}", id)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void findByExistingId() {
        UUID id = UUID.randomUUID();
        CompanyDto dto = CompanyDto.builder()
                .id(id)
                .fullName(RandomTestUtils.randomString("name"))
                .status(CompanyStatus.ACTIVE)
                .build();
        when(companyService.findById(id))
                .thenReturn(Mono.just(dto));

        webClient.get()
                .uri("/api/companies/{id}", id)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(CompanyDto.class)
                .isEqualTo(dto);
    }

    @Test
    public void findFullDetailsByExistingId() {
        UUID id = UUID.randomUUID();
        FullDetailsCompanyDto dto = FullDetailsCompanyDto.builder()
                .id(id)
                .fullName(RandomTestUtils.randomString("name"))
                .status(CompanyStatus.ACTIVE)
                .address(RandomTestUtils.randomString("address"))
                .site(RandomTestUtils.randomString("site"))
                .email(RandomTestUtils.randomEmail())
                .country("BY")
                .city("Minsk")
                .vatin(RandomTestUtils.randomString("BY"))
                .build();
        when(companyService.findFullDetailsById(id))
                .thenReturn(Mono.just(dto));

        webClient.get()
                .uri("/api/companies/{id}/full-details", id)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(FullDetailsCompanyDto.class)
                .isEqualTo(dto);
    }

}