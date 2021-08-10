package com.acme.usersrv.common.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
@Schema(name = "Login response")
public class LoginResponseDto {
    private String accessToken;

    @Tolerate
    public LoginResponseDto() {
    }
}
