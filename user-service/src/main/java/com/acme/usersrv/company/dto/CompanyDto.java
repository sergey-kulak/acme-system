package com.acme.usersrv.company.dto;

import com.acme.usersrv.company.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import java.util.UUID;

@Data
@Builder
@Schema(name = "Company")
public class CompanyDto {
    private UUID id;
    private String fullName;
    private CompanyStatus status;

    @Tolerate
    public CompanyDto() {
    }
}
