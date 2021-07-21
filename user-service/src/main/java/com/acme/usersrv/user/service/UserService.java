package com.acme.usersrv.user.service;

import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UpdateUserDto;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface UserService {
    // TODO ??? change
    Mono<UUID> create(@Valid CreateUserDto saveDto);

    Mono<Boolean> existsByEmail(String email);

    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Page<UserDto>> find(UserFilter userFilter, Pageable pageable);

    Mono<UserDto> findById(UUID id);

    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY_OWNER')")
    Mono<Void> update(UUID id, @Valid UpdateUserDto dto);
}
