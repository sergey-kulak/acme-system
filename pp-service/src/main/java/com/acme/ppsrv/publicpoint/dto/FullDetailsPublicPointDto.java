package com.acme.ppsrv.publicpoint.dto;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FullDetailsPublicPointDto {
    private UUID id;
    private UUID companyId;
    private PublicPointStatus status;
    private String name;
    private String description;
    private String city;
    private String address;
    private String primaryLang;
    private List<String> langs;
    private String currency;
}
