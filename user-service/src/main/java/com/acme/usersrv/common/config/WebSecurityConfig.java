package com.acme.usersrv.common.config;

import com.acme.commons.security.BearerAuthenticationConverter;
import com.acme.usersrv.common.security.GenerateTokenSuccessHandler;
import com.acme.usersrv.common.security.JsonAuthenticationConverter;
import com.acme.commons.security.ParseTokenService;
import com.acme.commons.security.ParseTokenServiceImpl;
import com.acme.commons.security.TokenService;
import com.acme.commons.security.TokenServiceImpl;
import com.acme.usersrv.user.repository.UserRepository;
import com.acme.usersrv.common.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class WebSecurityConfig {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment env;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilterBefore(bearerTokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jsonBodyAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter jsonBodyAuthenticationFilter() {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(authenticationManager());
        webFilter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/auth/login"));
        webFilter.setServerAuthenticationConverter(new JsonAuthenticationConverter());
        webFilter.setAuthenticationSuccessHandler(generateTokenSuccessHandler());
        webFilter.setAuthenticationFailureHandler(defaultAuthenticationFailureHandler());
        return webFilter;
    }

    @Bean
    public AuthenticationWebFilter bearerTokenAuthenticationFilter() {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter((ReactiveAuthenticationManager) Mono::just);
        webFilter.setServerAuthenticationConverter(bearerAuthenticationConverter());
        webFilter.setAuthenticationFailureHandler(defaultAuthenticationFailureHandler());
        return webFilter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public ServerAuthenticationConverter bearerAuthenticationConverter() {
        return new BearerAuthenticationConverter(parseTokenService());
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public ServerAuthenticationSuccessHandler generateTokenSuccessHandler() {
        return new GenerateTokenSuccessHandler(objectMapper, tokenService());
    }

    @Bean
    public ServerAuthenticationFailureHandler defaultAuthenticationFailureHandler() {
        return (exchange, authentication) -> Mono.fromRunnable(() ->
                exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public TokenService tokenService() {
        return new TokenServiceImpl(
                env.getProperty("user-srv.security.jwt.private-key"),
                env.getProperty("user-srv.security.jwt.ttlInSec", Long.class, 1200L)
        );
    }

    @Bean
    public ParseTokenService parseTokenService() {
        return new ParseTokenServiceImpl(env.getProperty("user-srv.security.jwt.public-key"));
    }

}
