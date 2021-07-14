package com.acme.usersrv.user.service;

import com.acme.usersrv.user.User;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface UserService {
    Mono<UUID> create(@Valid CreateUserDto saveDto);

    Mono<Boolean> existsByEmail(String email);

    Mono<Page<User>> find(UserFilter userFilter, Pageable pageable);
}
