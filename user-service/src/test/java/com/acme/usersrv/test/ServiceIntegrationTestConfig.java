package com.acme.usersrv.test;

import com.acme.commons.security.CompanyUser;
import com.acme.commons.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.ReactiveTransactionManager;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
@ComponentScan()
public class ServiceIntegrationTestConfig {
    @Autowired
    private ReactiveTransactionManager rxTxManager;

    @Bean
    public TestEntityHelper testEntityHelper() {
        return new TestEntityHelper();
    }

    @PostConstruct
    public void postConstruct() {
        Transactions.init(rxTxManager);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            String roleText = username.substring(0, username.indexOf("@"));
            UserRole role = UserRole.valueOf(roleText.toUpperCase());
            UUID companyId = role == UserRole.ADMIN ? null : UUID.randomUUID();
            return new CompanyUser(UUID.randomUUID(), companyId, username.toLowerCase(), "qwe123", role);
        };
    }
}
