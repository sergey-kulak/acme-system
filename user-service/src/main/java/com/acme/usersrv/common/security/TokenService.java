package com.acme.usersrv.common.security;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateAccessToken(Authentication authentication);
}
