package com.acme.accountingsrv.plan.dto;

import com.acme.accountingsrv.plan.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "Plan with countries")
public class PlanWithCountriesDto {
    private UUID id;
    private String name;
    private String description;
    private PlanStatus status;
    private int maxTableCount;
    private BigDecimal monthPrice;
    private String currency;
    private BigDecimal upfrontDiscount6m;
    private BigDecimal upfrontDiscount1y;
    private List<String> countries;
}
