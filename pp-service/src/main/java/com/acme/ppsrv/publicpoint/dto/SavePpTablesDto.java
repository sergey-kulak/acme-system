package com.acme.ppsrv.publicpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SavePpTablesDto {
    @NotNull
    private UUID publicPointId;
    @Valid
    private List<SavePpTableDto> changed;
    private List<UUID> deleted;

    @Tolerate
    public SavePpTablesDto() {
    }
}
