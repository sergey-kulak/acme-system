package com.acme.accountingsrv.company.event;

import com.acme.accountingsrv.company.CompanyStatus;
import lombok.Value;

import java.util.UUID;

@Value
public class CompanyStatusChangedEvent {
    UUID companyId;
    CompanyStatus fromStatus;
    CompanyStatus toStatus;
}
