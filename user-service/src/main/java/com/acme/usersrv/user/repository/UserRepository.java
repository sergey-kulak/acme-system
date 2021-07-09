package com.acme.usersrv.user.repository;

import com.acme.usersrv.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveSortingRepository<User, UUID> {

    @Query("select * from \"user\" where company_id = $1 and role = 'COMPANY_OWNER'")
    Flux<User> findCompanyOwners(UUID companyId);

    Mono<Boolean> existsByEmail(String email);
}
