package com.acme.usersrv.user.dto;

import com.acme.usersrv.user.dto.validation.CompanyIdRoleCheck;
import com.acme.usersrv.user.dto.validation.PasswordHandler;
import com.acme.usersrv.user.dto.validation.PasswordMatch;
import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.dto.validation.PublicPointIdCheck;
import com.acme.usersrv.user.dto.validation.PublicPointIdHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@PasswordMatch
@CompanyIdRoleCheck
@PublicPointIdCheck
@Schema(name = "Create user request")
public class CreateUserDto implements PasswordHandler, PublicPointIdHandler {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email
    private String email;
    private String phone;
    @NotBlank
    @Size(min = 6)
    private String password;
    private String confirmPassword;
    private UUID companyId;
    @NotNull
    private UserRole role;
    private UUID publicPointId;

    @Tolerate
    public CreateUserDto() {
    }
}
