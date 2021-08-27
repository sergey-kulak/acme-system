package com.acme.accountingsrv.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CompanyPlanDto {
    private UUID id;
    private UUID companyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private PlanDto plan;
}
