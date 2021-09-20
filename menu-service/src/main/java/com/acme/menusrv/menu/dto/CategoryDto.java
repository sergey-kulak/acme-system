package com.acme.menusrv.menu.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private UUID companyId;
    private UUID publicPointId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int position;
    private Set<DayOfWeek> days;
    private List<UUID> dishIds;
}
