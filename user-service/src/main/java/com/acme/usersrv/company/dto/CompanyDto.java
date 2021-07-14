package com.acme.usersrv.company.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.UUID;

@Data
@Builder
public class CompanyDto {
    private UUID id;
    private String fullName;

    @Tolerate
    public CompanyDto() {
    }
}
