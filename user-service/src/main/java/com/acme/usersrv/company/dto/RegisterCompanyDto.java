package com.acme.usersrv.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@Schema(name = "Company registration request")
public class RegisterCompanyDto {
    @NotBlank
    private String fullName;
    @NotBlank
    @Schema(description = "VAT identification number")
    private String vatin;
    @Schema(description = "Registration number number")
    private String regNumber;
    @Email
    private String email;
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    private String site;
    @NotBlank
    private String phone;
    @Valid
    private CreateOwnerDto owner;
    @NotNull
    private UUID planId;

    @Tolerate
    public RegisterCompanyDto() {
    }
}
