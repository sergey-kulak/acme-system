package com.acme.usersrv.common.security;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface ParseTokenService {

    Mono<Authentication> parseAccessToken(String accessToken);
}
