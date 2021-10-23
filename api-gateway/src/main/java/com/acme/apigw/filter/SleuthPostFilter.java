package com.acme.apigw.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Slf4j
public class SleuthPostFilter implements GlobalFilter {
    private static final String XTRACEID = "X-TraceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.deferContextual(context -> {
                    try {
                        TraceContext trxCtx = context.get(TraceContext.class);
                        exchange.getResponse().getHeaders()
                                .set(XTRACEID, trxCtx.traceId());
                    } catch (NoSuchElementException e) {
                        // skip because sleuth not enabled
                    }
                    return Mono.just(exchange);
                }))
                .then();
    }

}
