package com.acme.usersrv.user.service;

import com.acme.usersrv.company.dto.CreateOwnerDto;
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
    Mono<UUID> createCompanyOwner(UUID companyId, @Valid CreateOwnerDto createDto);

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_OWNER')")
    Mono<UUID> create(@Valid CreateUserDto createDto);

    Mono<Boolean> existsByEmail(String email);

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_OWNER', 'PP_MANAGER')")
    Mono<Page<UserDto>> find(UserFilter userFilter, Pageable pageable);

    @PreAuthorize("isAuthenticated()")
    Mono<UserDto> findById(UUID id);

    @PreAuthorize("isAuthenticated()")
    Mono<Void> update(UUID id, @Valid UpdateUserDto dto);
}
