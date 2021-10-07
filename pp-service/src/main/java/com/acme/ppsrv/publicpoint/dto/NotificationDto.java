package com.acme.ppsrv.publicpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationDto {
    private String type;
    private UUID companyId;
    private UUID publicPointId;
    private UUID tableId;
    private Map<String, Object> data;

    @Tolerate
    public NotificationDto() {
    }
}
