package com.acme.usersrv.user.service;

import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UUID> create(CreateUserDto saveDto);

    Mono<Boolean> existsByEmail(String email);

    Mono<Page<User>> find(UserFilter userFilter, Pageable pageable);
}
