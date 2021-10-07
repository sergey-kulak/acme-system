package com.acme.ppsrv.order.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class LiveOrderFilter {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;

    @Tolerate
    public LiveOrderFilter() {
    }
}
