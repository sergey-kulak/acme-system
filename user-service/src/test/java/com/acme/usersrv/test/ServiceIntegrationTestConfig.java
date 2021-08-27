package com.acme.usersrv.test;

import com.acme.testcommons.Transactions;
import com.acme.testcommons.security.TestUserDetailsService;
import com.acme.usersrv.company.event.CompanyEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.ReactiveTransactionManager;

import javax.annotation.PostConstruct;

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
        return new TestUserDetailsService();
    }
}
