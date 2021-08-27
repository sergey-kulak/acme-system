package com.acme.accountingsrv.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignPlanDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID planId;
}
