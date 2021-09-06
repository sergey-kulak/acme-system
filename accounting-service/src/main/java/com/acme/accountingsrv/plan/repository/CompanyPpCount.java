package com.acme.accountingsrv.plan.repository;

import lombok.Value;

import java.util.UUID;

@Value
public class CompanyPpCount {
    UUID companyId;
    long ppCount;
}
