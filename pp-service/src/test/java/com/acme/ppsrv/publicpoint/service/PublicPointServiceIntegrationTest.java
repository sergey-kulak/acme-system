package com.acme.ppsrv.publicpoint.service;

import com.acme.commons.exception.IllegalStatusChange;
import com.acme.ppsrv.plan.api.PublicPointPlanApi;
import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.dto.CreatePublicPointDto;
import com.acme.ppsrv.publicpoint.dto.FullDetailsPublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointDto;
import com.acme.ppsrv.publicpoint.dto.PublicPointFilter;
import com.acme.ppsrv.publicpoint.dto.UpdatePublicPointDto;
import com.acme.ppsrv.publicpoint.exception.PlanNotAssignedException;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.test.ServiceIntegrationTest;
import com.acme.ppsrv.test.TestEntityHelper;
import com.acme.testcommons.RandomTestUtils;
import com.acme.testcommons.TxStepVerifier;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.testcommons.security.WithMockAdmin;
import com.acme.testcommons.security.WithMockCompanyOwner;
import com.acme.testcommons.security.WithMockCook;
import com.acme.testcommons.security.WithMockPpManager;
import com.acme.testcommons.security.WithMockWaiter;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.acme.commons.utils.StreamUtils.mapToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ServiceIntegrationTest
class PublicPointServiceIntegrationTest {
    @Autowired
    PublicPointService ppService;
    @Autowired
    PublicPointRepository ppRepository;
    @Autowired
    TestEntityHelper testEntityHelper;
    @Autowired
    PublicPointPlanApi publicPointPlanApi;

    @Test
    @WithMockCompanyOwner
    void createValidation() {
        ppService.create(new CreatePublicPointDto())
                .as(StepVerifier::create)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void createDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(ppService.create(buildCreateDto(companyId)))
                .as(StepVerifier::create)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private CreatePublicPointDto buildCreateDto(UUID companyId) {
        return CreatePublicPointDto.builder()
                .companyId(companyId)
                .name(RandomTestUtils.randomString("Plan"))
                .description(RandomTestUtils.randomString("Descr"))
                .city(RandomTestUtils.randomString("City"))
                .address(RandomTestUtils.randomString("Address"))
                .primaryLang("EN")
                .langs(Set.of("RU", "BY"))
                .build();
    }

    private UpdatePublicPointDto buildUpdateDto() {
        return UpdatePublicPointDto.builder()
                .name(RandomTestUtils.randomString("Plan"))
                .description(RandomTestUtils.randomString("Descr"))
                .city(RandomTestUtils.randomString("City"))
                .address(RandomTestUtils.randomString("Address"))
                .primaryLang("EN")
                .langs(Set.of("RU", "BY"))
                .build();
    }

    @Test
    @WithMockCompanyOwner
    void create() {
        UUID companyId = UUID.randomUUID();
        CreatePublicPointDto saveDto = buildCreateDto(companyId);
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(ppService.create(saveDto))
                .flatMap(id -> Mono.zip(
                        ppRepository.findById(id),
                        ppRepository.getLangs(id)
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint publicPoint = data.getT1();
                    assertThat(publicPoint, allOf(
                            hasProperty("name", is(saveDto.getName())),
                            hasProperty("description", is(saveDto.getDescription())),
                            hasProperty("status", is(PublicPointStatus.INACTIVE)),
                            hasProperty("city", is(saveDto.getCity())),
                            hasProperty("address", is(saveDto.getAddress())),
                            hasProperty("primaryLang", is(saveDto.getPrimaryLang().toLowerCase()))
                    ));
                    List<String> langs = data.getT2();
                    assertEquals(mapToList(saveDto.getLangs(), String::toLowerCase), langs);
                })
                .verifyComplete();
    }

    @Test
    @WithMockCook
    void findByIdDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> ppService.findFullDetailsById(pp.getId()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();

    }

    @Test
    @WithMockCompanyOwner
    void findFullDetailsById() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .zipWhen(pp -> Mono.zip(
                        ppService.findFullDetailsById(pp.getId()),
                        ppRepository.getLangs(pp.getId())
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint publicPoint = data.getT1();
                    FullDetailsPublicPointDto ppDto = data.getT2().getT1();
                    List<String> langs = data.getT2().getT2();

                    assertThat(ppDto, allOf(
                            hasProperty("name", is(publicPoint.getName())),
                            hasProperty("description", is(publicPoint.getDescription())),
                            hasProperty("status", is(publicPoint.getStatus())),
                            hasProperty("city", is(publicPoint.getCity())),
                            hasProperty("address", is(publicPoint.getAddress())),
                            hasProperty("primaryLang", is(publicPoint.getPrimaryLang().toLowerCase()))
                    ));
                    assertEquals(langs, ppDto.getLangs());
                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void updateDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> ppService.update(pp.getId(), buildUpdateDto()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void update() {
        UUID companyId = UUID.randomUUID();
        UpdatePublicPointDto saveDto = buildUpdateDto();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .zipWhen(pp -> ppService.update(pp.getId(), saveDto)
                        .then(Mono.zip(
                                ppRepository.findById(pp.getId()),
                                ppRepository.getLangs(pp.getId())
                        )))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint publicPoint = data.getT1();
                    PublicPoint updatedPublicPoint = data.getT2().getT1();
                    assertThat(updatedPublicPoint, allOf(
                            hasProperty("name", is(saveDto.getName())),
                            hasProperty("description", is(saveDto.getDescription())),
                            hasProperty("status", is(publicPoint.getStatus())),
                            hasProperty("city", is(saveDto.getCity())),
                            hasProperty("address", is(saveDto.getAddress())),
                            hasProperty("primaryLang", is(saveDto.getPrimaryLang().toLowerCase()))
                    ));
                    List<String> langs = data.getT2().getT2();
                    assertEquals(mapToList(saveDto.getLangs(), String::toLowerCase), langs);
                })
                .verifyComplete();
    }

    private void allowedStatusChange(PublicPointStatus fromStatus, PublicPointStatus toStatus) {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId, fromStatus))
                .flatMap(pp -> ppService.changeStatus(pp.getId(), toStatus)
                        .then(ppRepository.findById(pp.getId())))
                .as(TxStepVerifier::withRollback)
                .assertNext(pp -> assertEquals(toStatus, pp.getStatus()))
                .verifyComplete();
    }

    private void disallowedStatusChange(PublicPointStatus fromStatus, PublicPointStatus toStatus) {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId, fromStatus))
                .flatMap(pp -> ppService.changeStatus(pp.getId(), toStatus))
                .as(TxStepVerifier::withRollback)
                .expectError(IllegalStatusChange.class)
                .verify();
    }

    @Test
    void changeStatusDenied() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> ppService.changeStatus(pp.getId(), PublicPointStatus.ACTIVE))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void inactiveStatusToActiveDenied() {
        when(publicPointPlanApi.findActivePlanId(any()))
                .thenReturn(Mono.empty());
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId, PublicPointStatus.INACTIVE))
                .flatMap(pp -> ppService.changeStatus(pp.getId(), PublicPointStatus.ACTIVE))
                .as(TxStepVerifier::withRollback)
                .expectError(PlanNotAssignedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void inactiveStatusToActive() {
        when(publicPointPlanApi.findActivePlanId(any()))
                .thenReturn(Mono.just(UUID.randomUUID()));
        allowedStatusChange(PublicPointStatus.INACTIVE, PublicPointStatus.ACTIVE);
    }

    @Test
    @WithMockCompanyOwner
    void activeStatusToInactive() {
        allowedStatusChange(PublicPointStatus.ACTIVE, PublicPointStatus.INACTIVE);
    }

    @Test
    @WithMockCompanyOwner
    void activeStatusToStopped() {
        allowedStatusChange(PublicPointStatus.ACTIVE, PublicPointStatus.STOPPED);
    }

    @Test
    @WithMockCompanyOwner
    void inactiveStatusToStopped() {
        allowedStatusChange(PublicPointStatus.INACTIVE, PublicPointStatus.STOPPED);
    }

    @Test
    @WithMockCompanyOwner
    void activeStatusToSuspend() {
        disallowedStatusChange(PublicPointStatus.ACTIVE, PublicPointStatus.SUSPENDED);
    }

    @Test
    @WithMockAdmin
    void activeStatusToSuspendAdmin() {
        allowedStatusChange(PublicPointStatus.ACTIVE, PublicPointStatus.SUSPENDED);
    }

    @Test
    @WithMockCompanyOwner
    void suspendStatusToActive() {
        disallowedStatusChange(PublicPointStatus.SUSPENDED, PublicPointStatus.ACTIVE);
    }

    @Test
    @WithMockAdmin
    void suspendStatusToActiveAdmin() {
        when(publicPointPlanApi.findActivePlanId(any()))
                .thenReturn(Mono.just(UUID.randomUUID()));
        allowedStatusChange(PublicPointStatus.SUSPENDED, PublicPointStatus.ACTIVE);
    }

    @Test
    @WithMockAdmin
    void stoppedStatusToActiveAdmin() {
        disallowedStatusChange(PublicPointStatus.STOPPED, PublicPointStatus.ACTIVE);
    }

    private PublicPointFilter createFilter(PublicPoint publicPoint) {
        return PublicPointFilter.builder()
                .companyId(publicPoint.getCompanyId())
                .namePattern(publicPoint.getName())
                .status(List.of(publicPoint.getStatus()))
                .build();
    }

    @Test
    @WithMockCompanyOwner
    void findDenied() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> ppService.find(createFilter(pp), pageable))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void findDeniedForAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(ppService.find(PublicPointFilter.builder().build(), pageable))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockCompanyOwner
    void find() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .zipWhen(pp -> Mono.zip(
                        ppService.find(createFilter(pp), pageable),
                        ppRepository.getLangs(pp.getId())
                ))
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint publicPoint = data.getT1();
                    Page<FullDetailsPublicPointDto> page = data.getT2().getT1();

                    assertEquals(1, page.getTotalElements());
                    FullDetailsPublicPointDto dto = page.getContent().get(0);

                    assertThat(dto, allOf(
                            hasProperty("name", is(publicPoint.getName())),
                            hasProperty("description", is(publicPoint.getDescription())),
                            hasProperty("status", is(publicPoint.getStatus())),
                            hasProperty("city", is(publicPoint.getCity())),
                            hasProperty("address", is(publicPoint.getAddress())),
                            hasProperty("primaryLang", is(publicPoint.getPrimaryLang().toLowerCase()))
                    ));
                    List<String> langs = data.getT2().getT2();
                    assertEquals(langs, dto.getLangs());
                })
                .verifyComplete();
    }

    @Test
    @WithMockCompanyOwner
    void findNames() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId, PublicPointStatus.STOPPED))
                .then(testEntityHelper.createPublicPoint(companyId))
                .zipWhen(pp -> ppService.findNames(companyId).collectList())
                .as(TxStepVerifier::withRollback)
                .assertNext(data -> {
                    PublicPoint publicPoint = data.getT1();
                    List<PublicPointDto> items = data.getT2();

                    assertEquals(1, items.size());
                    PublicPointDto dto = items.get(0);

                    assertThat(dto, allOf(
                            hasProperty("id", is(publicPoint.getId())),
                            hasProperty("name", is(publicPoint.getName())),
                            hasProperty("companyId", is(publicPoint.getCompanyId())),
                            hasProperty("status", is(publicPoint.getStatus()))
                    ));
                })
                .verifyComplete();

    }

    @Test
    @WithMockPpManager
    void findById() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(pp -> TestSecurityUtils.linkPpWithCurrentUserReturn(pp.getId(), pp))
                .zipWhen(pp -> ppService.findById(pp.getId()))
                .as(TxStepVerifier::withRollback)
                .assertNext(pp -> {
                    PublicPoint publicPoint = pp.getT1();
                    PublicPointDto ppDto = pp.getT2();

                    assertThat(ppDto, allOf(
                            hasProperty("id", is(publicPoint.getId())),
                            hasProperty("name", is(publicPoint.getName())),
                            hasProperty("status", is(publicPoint.getStatus())),
                            hasProperty("companyId", is(publicPoint.getCompanyId()))
                    ));
                })
                .verifyComplete();
    }


    @Test
    @WithMockPpManager
    void findFullDetailsByIdOtherPp() {
        UUID companyId = UUID.randomUUID();
        TestSecurityUtils.linkWithCurrentUser(companyId)
                .then(testEntityHelper.createPublicPoint(companyId))
                .flatMap(TestSecurityUtils::linkOtherPpWithCurrentUserReturn)
                .zipWhen(pp -> ppService.findById(pp.getId()))
                .as(TxStepVerifier::withRollback)
                .expectError(AccessDeniedException.class)
                .verify();
    }
}