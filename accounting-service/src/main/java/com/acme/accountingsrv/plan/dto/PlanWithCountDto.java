package com.acme.accountingsrv.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Plan with company count")
public class PlanWithCountDto extends PlanWithCountriesDto {
    private int companyCount;
}
