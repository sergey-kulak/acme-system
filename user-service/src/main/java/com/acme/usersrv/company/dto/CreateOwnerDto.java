package com.acme.usersrv.company.dto;

import com.acme.usersrv.user.dto.validation.PasswordHandler;
import com.acme.usersrv.user.dto.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@PasswordMatch
@Schema(name = "Owner info")
public class CreateOwnerDto implements PasswordHandler {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    private String phone;
    @NotBlank
    @Size(min = 6)
    private String password;
    private String confirmPassword;

    @Tolerate
    public CreateOwnerDto() {
    }
}
