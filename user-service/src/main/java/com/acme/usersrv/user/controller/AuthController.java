package com.acme.usersrv.user.controller;

import com.acme.commons.openapi.SecureOperation;
import com.acme.usersrv.common.security.LoginResponseDto;
import com.acme.commons.security.TokenService;
import com.acme.commons.security.SecurityUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Api", description = "Authentication Api")
public class AuthController {
    private final TokenService tokenService;

    @PostMapping("/refresh")
    @SecureOperation(description = "Refresh access token")
    @ApiResponse(responseCode = "200")
    public Mono<LoginResponseDto> refreshAccessToken() {
        return SecurityUtils.getAuthentication()
                .map(tokenService::generateAccessToken)
                .map(token -> LoginResponseDto.builder()
                        .accessToken(token)
                        .build());
    }
}
