package com.acme.usersrv.company.dto;

import com.acme.usersrv.company.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Company status request")
public class CompanyStatusDto {
    @NotNull
    private CompanyStatus status;
}
