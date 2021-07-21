package com.acme.usersrv.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableReactiveMethodSecurity
public class ServiceSecurityConfig {
    @Value("${user-srv.security.hash-strength: 5}")
    private int hashStrength;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(hashStrength);
    }
}
