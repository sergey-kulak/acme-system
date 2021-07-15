package com.acme.usersrv.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Schema(name = "Update Company request")
public class UpdateCompanyDto {
    @Email
    private String email;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    private String site;
    @NotBlank
    private String phone;
}
