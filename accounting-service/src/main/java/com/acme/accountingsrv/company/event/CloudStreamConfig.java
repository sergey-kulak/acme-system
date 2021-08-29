package com.acme.accountingsrv.company.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class CloudStreamConfig {
    @Autowired
    private CompanyEventService companyEventService;

    @Bean
    public Function<Flux<CompanyRegisteredEvent>, Mono<Void>> companyRegisteredConsumer() {
        return consume(companyEventService::onEvent);
    }

    private <T> Function<Flux<T>, Mono<Void>> consume(Function<T, Mono<Void>> consumer) {
        return eventMono -> eventMono.flatMap(consumer).then();
    }

    @Bean
    public Function<Flux<CompanyStatusChangedEvent>, Mono<Void>> companyStatusConsumer() {
        return consume(companyEventService::onEvent);
    }

}
