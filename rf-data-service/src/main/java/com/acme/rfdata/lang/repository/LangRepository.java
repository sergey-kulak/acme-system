package com.acme.rfdata.lang.repository;

import com.acme.rfdata.country.Country;
import com.acme.rfdata.currency.Currency;
import com.acme.rfdata.lang.Lang;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface LangRepository extends ReactiveMongoRepository<Lang, String> {

    @Query(value = "{ 'active' : true }", sort = "{ name : 1 }")
    Flux<Lang> findAllActive();
}
