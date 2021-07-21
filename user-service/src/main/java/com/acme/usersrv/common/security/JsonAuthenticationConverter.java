package com.acme.usersrv.common.security;

import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;

import static org.springframework.core.ResolvableType.forClass;

public class JsonAuthenticationConverter implements ServerAuthenticationConverter {
    private final Jackson2JsonDecoder DECODER = new Jackson2JsonDecoder();
    private final ResolvableType ELEMENT_TYPE = forClass(LoginRequestDto.class);

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest()
                .getBody()
                .map(Flux::just)
                .map(body -> DECODER.decodeToMono(body, ELEMENT_TYPE,
                        MediaType.APPLICATION_JSON, Collections.emptyMap()).cast(LoginRequestDto.class))
                .next()
                .flatMap(Function.identity())
                .map(this::toAuthentication);

    }

    private UsernamePasswordAuthenticationToken toAuthentication(LoginRequestDto data) {
        String username = data.getUsername();
        String password = data.getPassword();
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}
