package com.acme.rfdatasrv.country.repository;

import com.acme.rfdatasrv.country.Country;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CountryRepository extends ReactiveMongoRepository<Country, String> {

    @Query(value = "{ 'active' : true }", sort = "{ name : 1 }")
    Flux<Country> findAllActive();
}
