package com.acme.usersrv.user.dto;

import com.acme.commons.security.UserRole;
import com.acme.usersrv.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(name = "User")
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID companyId;
    private UserRole role;
    private UserStatus status;
}
