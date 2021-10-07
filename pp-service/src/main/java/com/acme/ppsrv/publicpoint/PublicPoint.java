package com.acme.ppsrv.publicpoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("public_point")
@Getter
@Setter
public class PublicPoint {
    @Id
    private UUID id;
    private UUID companyId;
    private PublicPointStatus status;
    private String name;
    private String description;
    private String city;
    private String address;
    private String primaryLang;
    private String currency;
}
