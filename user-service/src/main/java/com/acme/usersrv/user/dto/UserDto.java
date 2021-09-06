package com.acme.usersrv.user.dto;

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
}
