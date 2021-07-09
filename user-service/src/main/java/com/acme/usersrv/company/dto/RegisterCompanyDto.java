package com.acme.usersrv.company.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class RegisterCompanyDto {
    @NotBlank
    private String fullName;
    @NotBlank
    private String vatin;
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
    private SaveOwnerDto owner;

    @Tolerate
    public RegisterCompanyDto() {
    }
}
