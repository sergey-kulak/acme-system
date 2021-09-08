package com.acme.ppsrv.plan.dto;

import com.acme.ppsrv.plan.PlanStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
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

    @Tolerate
    public PlanWithCountriesDto() {
    }
}
