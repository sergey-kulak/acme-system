package com.acme.usersrv.company.event;

import com.acme.usersrv.company.CompanyStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.UUID;

@Data
@Builder
public class CompanyStatusChangedEvent {
    private UUID companyId;
    private CompanyStatus fromStatus;
    private CompanyStatus toStatus;

    @Tolerate
    public CompanyStatusChangedEvent() {
    }
}
