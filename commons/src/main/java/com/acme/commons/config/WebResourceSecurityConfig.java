package com.acme.commons.config;


import com.acme.commons.security.BearerAuthenticationConverter;
import com.acme.commons.security.ParseTokenService;
import com.acme.commons.security.ParseTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public abstract class WebResourceSecurityConfig {
    @Autowired
    private Environment env;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilterBefore(bearerTokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter bearerTokenAuthenticationFilter() {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter((ReactiveAuthenticationManager) Mono::just);
        webFilter.setServerAuthenticationConverter(bearerAuthenticationConverter());
        webFilter.setAuthenticationFailureHandler(defaultAuthenticationFailureHandler());
        return webFilter;
    }

    @Bean
    public ServerAuthenticationConverter bearerAuthenticationConverter() {
        return new BearerAuthenticationConverter(parseTokenService());
    }

    @Bean
    public ServerAuthenticationFailureHandler defaultAuthenticationFailureHandler() {
        return (exchange, authentication) -> Mono.fromRunnable(() ->
                exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public ParseTokenService parseTokenService() {
        return new ParseTokenServiceImpl(env.getProperty(getPublicKeyProperty()));
    }

    protected abstract String getPublicKeyProperty();
}
