package com.acme.rfdata.country.repository;

import com.acme.rfdata.country.Country;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CountryRepository extends ReactiveMongoRepository<Country, String> {

    @Query(value = "{ 'active' : true }", sort = "{ name : 1 }")
    Flux<Country> findAllActive();
}
