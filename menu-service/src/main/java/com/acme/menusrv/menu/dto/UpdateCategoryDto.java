package com.acme.menusrv.menu.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UpdateCategoryDto {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<DayOfWeek> days;
    private List<UUID> dishIds;

    @Tolerate
    public UpdateCategoryDto() {
    }
}
