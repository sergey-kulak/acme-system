package com.acme.ppsrv.publicpoint.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PublicPointTableDto {
    private UUID id;
    private String name;
    private UUID publicPointId;
    private int seatCount;
    private String description;
}
