package com.acme.usersrv.company.dto;

import com.acme.usersrv.company.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
@Builder
@Schema(name = "Full details company")
@EqualsAndHashCode
public class FullDetailsCompanyDto {
    private UUID id;
    private String fullName;
    private CompanyStatus status;
    private String vatin;
    private String regNumber;
    private String email;
    private String country;
    private String city;
    private String address;
    private String site;
    private String phone;

    @Tolerate
    public FullDetailsCompanyDto() {
    }
}
