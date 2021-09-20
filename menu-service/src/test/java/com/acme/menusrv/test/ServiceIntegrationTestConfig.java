package com.acme.menusrv.test;

import com.acme.testcommons.security.TestUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@ComponentScan()
public class ServiceIntegrationTestConfig {

    @Bean
    public TestEntityHelper testEntityHelper() {
        return new TestEntityHelper();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new TestUserDetailsService();
    }

}
