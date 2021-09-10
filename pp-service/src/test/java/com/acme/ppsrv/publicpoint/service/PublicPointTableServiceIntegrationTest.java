package com.acme.ppsrv.publicpoint.service;

import com.acme.ppsrv.plan.api.PublicPointPlanApi;
import com.acme.ppsrv.plan.dto.PlanWithCountriesDto;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.dto.PublicPointTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTableDto;
import com.acme.ppsrv.publicpoint.dto.SavePpTablesDto;
import com.acme.ppsrv.publicpoint.exception.PlanTableLimitExceededException;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.ppsrv.test.ServiceIntegrationTest;
import com.acme.ppsrv.test.TestEntityHelper;
import com.acme.testcommons.TxStepVerifier;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockCompanyOwner;
import com.acme.testcommons.security.WithMockPpManager;
import com.acme.testcommons.security.WithMockWaiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ServiceIntegrationTest
class PublicPointTableServiceIntegrationTest {
    @Autowired
    PublicPointTableService ppTableService;
    @Autowired
    PublicPointTableRepository ppTableRepository;
    @Autowired
    PublicPointPlanApi publicPointPlanApi;
    @Autowired
    TestEntityHelper testEntityHelper;

    @Test
    @WithMockCompanyOwner
    void saveValidation() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(ppTableService.save(new SavePpTablesDto()))
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void saveNoUserDenied() {
        ppTableService.save(new SavePpTablesDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    private SavePpTableDto createSavePpTableDto() {
        return SavePpTableDto.builder()
                .name("table")
                .description("descr")
                .seatCount(5)
                .build();
    }

    private void mockWithPlan(Integer maxTableCount) {
        if (maxTableCount == null) {
            when(publicPointPlanApi.findActivePlan(any()))
                    .thenReturn(Mono.empty());
        } else {
            when(publicPointPlanApi.findActivePlan(any()))
                    .thenReturn(Mono.just(PlanWithCountriesDto.builder()
                            .id(UUID.randomUUID())
                            .maxTableCount(maxTableCount)
                            .build()));
        }
    }

    @Test
    @WithMockPpManager
    void saveNoPlan() {
        UUID companyId = UUID.randomUUID();
        mockWithPlan(null);
        SavePpTableDto itemDto = createSavePpTableDto();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> {
                    SavePpTablesDto dto = SavePpTablesDto.builder()
                            .publicPointId(pp.getId())
                            .changed(List.of(itemDto))
                            .build();
                    return ppTableService.save(dto)
                            .thenReturn(pp);
                })
                .as(TxStepVerifier::withRollback)
                .expectError(PlanTableLimitExceededException.class)
                .verify();

    }

    @Test
    @WithMockPpManager
    void saveExceedPlan() {
        UUID companyId = UUID.randomUUID();
        mockWithPlan(1);
        SavePpTableDto itemDto = createSavePpTableDto();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> {
                    SavePpTablesDto dto = SavePpTablesDto.builder()
                            .publicPointId(pp.getId())
                            .changed(List.of(itemDto, itemDto))
                            .build();
                    return ppTableService.save(dto)
                            .thenReturn(pp);
                })
                .as(TxStepVerifier::withRollback)
                .expectError(PlanTableLimitExceededException.class)
                .verify();

    }

    @Test
    @WithMockPpManager
    void saveCreate() {
        UUID companyId = UUID.randomUUID();
        mockWithPlan(5);
        SavePpTableDto itemDto = createSavePpTableDto();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> {
                    SavePpTablesDto dto = SavePpTablesDto.builder()
                            .publicPointId(pp.getId())
                            .changed(List.of(itemDto))
                            .build();
                    return ppTableService.save(dto)
                            .thenReturn(pp);
                })
                .zipWhen(pp -> ppTableRepository.findByPublicPointIdOrderByName(pp.getId()).collectList())
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint pp = data.getT1();
                    List<PublicPointTable> tables = data.getT2();

                    assertEquals(1, tables.size());
                    assertThat(tables.get(0), allOf(
                            hasProperty("name", is(itemDto.getName())),
                            hasProperty("description", is(itemDto.getDescription())),
                            hasProperty("publicPointId", is(pp.getId())),
                            hasProperty("seatCount", is(itemDto.getSeatCount()))
                    ));
                })
                .verifyComplete();

    }

    @Test
    @WithMockPpManager
    void saveUpdate() {
        UUID companyId = UUID.randomUUID();
        mockWithPlan(5);
        SavePpTableDto itemDto = createSavePpTableDto();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table -> {
                    SavePpTablesDto dto = SavePpTablesDto.builder()
                            .publicPointId(table.getPublicPointId())
                            .changed(List.of(itemDto.toBuilder()
                                    .id(table.getId())
                                    .build()))
                            .build();
                    return ppTableService.save(dto)
                            .thenReturn(table);
                })
                .zipWhen(table ->
                        ppTableRepository.findByPublicPointIdOrderByName(table.getPublicPointId()).collectList())
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPointTable oldTable = data.getT1();
                    List<PublicPointTable> tables = data.getT2();

                    assertEquals(1, tables.size());
                    assertThat(tables.get(0), allOf(
                            hasProperty("id", is(oldTable.getId())),
                            hasProperty("name", is(itemDto.getName())),
                            hasProperty("description", is(itemDto.getDescription())),
                            hasProperty("publicPointId", is(oldTable.getPublicPointId())),
                            hasProperty("seatCount", is(itemDto.getSeatCount()))
                    ));
                })
                .verifyComplete();
    }

    @Test
    @WithMockPpManager
    void saveDelete() {
        UUID companyId = UUID.randomUUID();
        mockWithPlan(5);
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table -> {
                    SavePpTablesDto dto = SavePpTablesDto.builder()
                            .publicPointId(table.getPublicPointId())
                            .deleted(List.of(table.getId()))
                            .build();
                    return ppTableService.save(dto)
                            .thenReturn(table);
                })
                .flatMap(table ->
                        ppTableRepository.findByPublicPointIdOrderByName(table.getPublicPointId()).collectList())
                .as(TxStepVerifier::withRollback)
                .assertNext(tables -> assertEquals(0, tables.size()))
                .verifyComplete();
    }

    @Test
    @WithMockWaiter
    void findAll() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .zipWhen(table -> ppTableService.findAll(table.getPublicPointId()).collectList())
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPointTable table = data.getT1();
                    List<PublicPointTableDto> tables = data.getT2();

                    assertThat(tables.get(0), allOf(
                            hasProperty("id", is(table.getId())),
                            hasProperty("name", is(table.getName())),
                            hasProperty("description", is(table.getDescription())),
                            hasProperty("publicPointId", is(table.getPublicPointId())),
                            hasProperty("seatCount", is(table.getSeatCount()))
                    ));
                })
                .verifyComplete();
    }

    private Mono<PublicPoint> linkWithUser(PublicPoint pp){
        return TestSecurityUtils.linkPpWithCurrentUserReturn(pp.getId(), pp);
    }

    @Test
    @WithMockPpManager
    void countAll() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(this::linkWithUser)
                .flatMap(pp -> testEntityHelper.createTable(pp.getId()))
                .flatMap(table -> ppTableService.countAll(table.getPublicPointId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(count -> assertEquals(1, count))
                .verifyComplete();
    }
}