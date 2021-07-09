package com.acme.usersrv.user.dto;

import com.acme.usersrv.common.validation.PasswordHandler;
import com.acme.usersrv.user.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.UUID;

@Data
@Builder
public class CreateUserDto implements PasswordHandler {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private UUID companyId;
    private UserRole role;

    @Tolerate
    public CreateUserDto() {
    }
}
