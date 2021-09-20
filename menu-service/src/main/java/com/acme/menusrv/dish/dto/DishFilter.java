package com.acme.menusrv.dish.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class DishFilter {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    private String namePattern;
    private boolean withDeleted;

    @Tolerate
    public DishFilter() {
    }
}
