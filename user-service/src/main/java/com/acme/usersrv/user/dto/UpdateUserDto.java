package com.acme.usersrv.user.dto;

import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.dto.validation.PasswordHandler;
import com.acme.usersrv.user.dto.validation.PasswordMatch;
import com.acme.usersrv.user.dto.validation.PublicPointIdCheck;
import com.acme.usersrv.user.dto.validation.PublicPointIdHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@PasswordMatch
@PublicPointIdCheck
@Schema(name = "Update user request")
public class UpdateUserDto implements PasswordHandler, PublicPointIdHandler {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phone;
    @Size(min = 6)
    private String password;
    private String confirmPassword;
    @NotNull
    private UserRole role;
    private UUID publicPointId;

    @Tolerate
    public UpdateUserDto() {
    }
}
