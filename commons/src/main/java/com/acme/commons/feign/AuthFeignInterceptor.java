package com.acme.commons.feign;

import com.acme.commons.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class AuthFeignInterceptor implements ReactiveHttpRequestInterceptor {
    private static final String BEARER_TYPE = "Bearer ";

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
        return SecurityUtils.getAuthentication()
                .map(Authentication::getDetails)
                .doOnNext(details -> applyAuthorizationHeader(request, details))
                .thenReturn(request);
    }


    private void applyAuthorizationHeader(ReactiveHttpRequest request, Object details) {
        if (details instanceof String && !request.headers().containsKey(AUTHORIZATION)) {
            request.headers().put(AUTHORIZATION, List.of(BEARER_TYPE + " " + details));
        }
    }
}
