package com.acme.usersrv.user.repository;

import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserNameFilter;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveSortingRepository<User, UUID>, UserRepositoryCustom {

    @Query("select * from \"user\" where company_id = $1 and role = 'COMPANY_OWNER'")
    Flux<User> findCompanyOwners(UUID companyId);

    @Query("select count(1) > 0 from \"user\" where email = $1 and status = 'ACTIVE'")
    Mono<Boolean> existsActiveByEmail(String email);

    @Query("select * from \"user\" where email = $1 and status = 'ACTIVE'")
    Mono<User> findActiveByEmail(String email);
}
