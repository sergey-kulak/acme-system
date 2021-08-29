package com.acme.accountingsrv.plan.dto;

import com.acme.accountingsrv.plan.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanFilter {
    private String namePattern;
    private List<PlanStatus> status;
    private String country;
    private boolean onlyGlobal = false;
    private Integer tableCount;
    private UUID companyId;

    @Tolerate
    public PlanFilter() {
    }
}
