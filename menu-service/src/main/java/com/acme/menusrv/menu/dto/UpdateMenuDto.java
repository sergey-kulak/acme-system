package com.acme.menusrv.menu.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UpdateMenuDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    private List<UUID> categoryIds;

    @Tolerate
    public UpdateMenuDto() {
    }
}
