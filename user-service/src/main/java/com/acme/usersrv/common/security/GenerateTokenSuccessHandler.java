package com.acme.usersrv.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GenerateTokenSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBuffer buffer = response.bufferFactory().wrap(buildBody(authentication));
        return response.writeWith(Flux.just(buffer));
    }

    @SneakyThrows
    private byte[] buildBody(Authentication authentication) {
        return objectMapper.writeValueAsBytes(LoginResponseDto.builder()
                .accessToken(tokenService.generateAccessToken(authentication))
                .build());
    }
}
