package com.acme.menusrv.menu;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Document("category")
public class Category {
    @Id
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
