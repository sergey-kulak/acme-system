package com.acme.accountingsrv.company.event;

import lombok.Value;

import java.util.UUID;

@Value
public class CompanyRegisteredEvent {
    UUID companyId;
    UUID planId;
}
