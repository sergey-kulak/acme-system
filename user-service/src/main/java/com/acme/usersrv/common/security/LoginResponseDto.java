package com.acme.usersrv.common.security;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class LoginResponseDto {
    private String accessToken;

    @Tolerate
    public LoginResponseDto() {
    }
}
