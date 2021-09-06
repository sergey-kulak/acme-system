package com.acme.usersrv.user.repository;

import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.dto.UserNameFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryCustom {
    Mono<Page<User>> find(UserFilter filter, Pageable pageable);

    Flux<UserDto> findNames(UserNameFilter filter);
}
