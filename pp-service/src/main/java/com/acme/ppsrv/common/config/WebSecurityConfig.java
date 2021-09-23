package com.acme.ppsrv.common.config;


import com.acme.commons.security.TokenService;
import com.acme.commons.security.TokenServiceImpl;
import com.acme.commons.security.WebResourceSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebSecurityConfig extends WebResourceSecurityConfig {
    @Autowired
    private Environment env;

    @Override
    protected String getPublicKeyProperty() {
        return "pp-srv.security.jwt.public-key";
    }

    @Bean
    public TokenService tokenService() {
        return new TokenServiceImpl(
                env.getProperty("pp-srv.security.jwt.private-key"),
                env.getProperty("pp-srv.security.jwt.ttlInSec", Long.class, 24 * 3600L)
        );
    }
}
