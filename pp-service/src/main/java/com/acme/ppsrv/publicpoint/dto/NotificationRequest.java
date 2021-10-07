package com.acme.ppsrv.publicpoint.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationRequest {
    private UUID companyId;
    private UUID publicPointId;
}
