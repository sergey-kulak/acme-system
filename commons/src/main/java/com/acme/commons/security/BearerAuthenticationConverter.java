package com.acme.commons.security;

import com.acme.commons.security.ParseTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BearerAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER_TYPE = "Bearer ";
    private final ParseTokenService parseTokenService;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        return Mono.justOrEmpty(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> StringUtils.startsWith(authHeader, BEARER_TYPE))
                .map(authHeader -> authHeader.substring(BEARER_TYPE.length()))
                .flatMap(parseTokenService::parseAccessToken);
    }
}
