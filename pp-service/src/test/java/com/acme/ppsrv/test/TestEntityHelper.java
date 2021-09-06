package com.acme.ppsrv.test;

import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.testcommons.RandomTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private PublicPointRepository ppRepository;

    public Mono<PublicPoint> createPublicPoint(UUID companyId) {
        return createPublicPoint(companyId, PublicPointStatus.ACTIVE);
    }

    public Mono<PublicPoint> createPublicPoint(UUID companyId, PublicPointStatus status) {
        PublicPoint publicPoint = new PublicPoint();
        publicPoint.setCompanyId(companyId);
        publicPoint.setStatus(status);
        publicPoint.setName(RandomTestUtils.randomString("Plan"));
        publicPoint.setDescription(RandomTestUtils.randomString("Descr"));
        publicPoint.setCity(RandomTestUtils.randomString("City"));
        publicPoint.setAddress(RandomTestUtils.randomString("Address"));
        publicPoint.setPrimaryLang("ru");

        return ppRepository.save(publicPoint)
                .flatMap(pp -> ppRepository.addLang(pp.getId(), "en")
                        .thenReturn(pp)
                );
    }
}
