package com.acme.accountingsrv.plan.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class PublicPointPlanDto {
    private UUID id;
    private UUID companyId;
    private UUID publicPointId;
    private Instant startDate;
    private Instant endDate;
    private PlanDto plan;
}
