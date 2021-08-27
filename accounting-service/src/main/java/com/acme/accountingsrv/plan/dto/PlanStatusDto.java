package com.acme.accountingsrv.plan.dto;

import com.acme.accountingsrv.plan.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Plan status request")
public class PlanStatusDto {
    @NotNull
    private PlanStatus status;
}
