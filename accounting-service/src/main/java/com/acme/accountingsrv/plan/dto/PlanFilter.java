package com.acme.accountingsrv.plan.dto;

import com.acme.accountingsrv.plan.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
@Builder
public class PlanFilter {
    private String namePattern;
    private List<PlanStatus> status;
    private String country;
    private boolean onlyGlobal = false;
    private Integer tableCount;

    @Tolerate
    public PlanFilter() {
    }
}
