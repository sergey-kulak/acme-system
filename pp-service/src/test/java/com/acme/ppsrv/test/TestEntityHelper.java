package com.acme.ppsrv.test;

import com.acme.ppsrv.publicpoint.PublicPoint;
import com.acme.ppsrv.publicpoint.PublicPointStatus;
import com.acme.ppsrv.publicpoint.PublicPointTable;
import com.acme.ppsrv.publicpoint.repository.PublicPointRepository;
import com.acme.ppsrv.publicpoint.repository.PublicPointTableRepository;
import com.acme.testcommons.RandomTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private PublicPointRepository ppRepository;
    @Autowired
    private PublicPointTableRepository ppTableRepository;

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

    public Mono<PublicPointTable> createTable(UUID publicPointId) {
        PublicPointTable table = new PublicPointTable();
        table.setName(RandomTestUtils.randomString("table"));
        table.setDescription(RandomTestUtils.randomString("descr"));
        table.setPublicPointId(publicPointId);
        table.setSeatCount(RandomUtils.nextInt(4, 20));

        return ppTableRepository.save(table);
    }
}
