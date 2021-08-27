package com.acme.accountingsrv.plan.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@AllArgsConstructor
@ToString
public class CompanyRegisteredEvent {
    private UUID companyId;
    private UUID planId;
}
