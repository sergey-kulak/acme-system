package com.acme.rfdatasrv.common.config;


import com.acme.commons.config.WebResourceSecurityConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig extends WebResourceSecurityConfig {

    @Override
    protected String getPublicKeyProperty() {
        return "rf-data-srv.security.jwt.public-key";
    }
}
