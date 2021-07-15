package com.acme.usersrv.user.dto;

import com.acme.usersrv.common.validation.PasswordHandler;
import com.acme.usersrv.common.validation.PasswordMatch;
import com.acme.usersrv.user.UserRole;
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
@Schema(name = "Update user request")
public class UpdateUserDto implements PasswordHandler {
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

    @Tolerate
    public UpdateUserDto() {
    }
}
