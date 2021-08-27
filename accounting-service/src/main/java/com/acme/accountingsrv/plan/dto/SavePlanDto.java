package com.acme.accountingsrv.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@Schema(name = "Save plan request")
public class SavePlanDto {
    @NotBlank
    private String name;
    private String description;
    @Positive
    private int maxTableCount;
    @NotNull
    @Positive
    private BigDecimal monthPrice;
    @NotBlank
    private String currency;
    @Positive
    @Max(100)
    private BigDecimal upfrontDiscount6m;
    @Positive
    @Max(100)
    private BigDecimal upfrontDiscount1y;
    private Set<String> countries;

    @Tolerate
    public SavePlanDto() {
    }
}
