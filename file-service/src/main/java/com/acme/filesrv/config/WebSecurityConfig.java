package com.acme.filesrv.config;


import com.acme.commons.security.WebResourceSecurityConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig extends WebResourceSecurityConfig {

    @Override
    protected String getPublicKeyProperty() {
        return "file-srv.security.jwt.public-key";
    }
}
