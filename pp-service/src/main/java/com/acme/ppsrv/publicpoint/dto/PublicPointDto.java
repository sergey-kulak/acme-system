package com.acme.ppsrv.publicpoint.dto;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicPointDto {
    private UUID id;
    private String name;
    private UUID companyId;
    private PublicPointStatus status;
}
