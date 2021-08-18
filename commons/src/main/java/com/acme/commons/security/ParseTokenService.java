package com.acme.commons.security;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface ParseTokenService {

    Mono<Authentication> parseAccessToken(String accessToken);
}
