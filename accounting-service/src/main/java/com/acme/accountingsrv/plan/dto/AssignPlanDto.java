package com.acme.accountingsrv.plan.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class AssignPlanDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    @NotNull
    private UUID planId;

    @Tolerate
    public AssignPlanDto() {
    }
}
