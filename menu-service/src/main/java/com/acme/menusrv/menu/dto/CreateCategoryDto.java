package com.acme.menusrv.menu.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CreateCategoryDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<DayOfWeek> days;
    private List<UUID> dishIds;

    @Tolerate
    public CreateCategoryDto() {
    }
}
