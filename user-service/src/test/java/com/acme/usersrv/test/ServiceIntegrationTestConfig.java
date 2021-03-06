package com.acme.usersrv.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;

import javax.annotation.PostConstruct;

@Configuration
public class ServiceIntegrationTestConfig {
    @Autowired
    private ReactiveTransactionManager rxTxManager;

    @Bean
    public TestEntityHelper testEntityHelper(){
        return new TestEntityHelper();
    }

    @PostConstruct
    public void postConstruct() {
        Transactions.init(rxTxManager);
    }
}
