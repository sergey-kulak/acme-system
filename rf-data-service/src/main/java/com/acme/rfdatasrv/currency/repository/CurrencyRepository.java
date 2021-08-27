package com.acme.rfdatasrv.currency.repository;

import com.acme.rfdatasrv.currency.Currency;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CurrencyRepository extends ReactiveMongoRepository<Currency, String> {

    @Query(value = "{ 'active' : true }", sort = "{ name : 1 }")
    Flux<Currency> findAllActive();
}
