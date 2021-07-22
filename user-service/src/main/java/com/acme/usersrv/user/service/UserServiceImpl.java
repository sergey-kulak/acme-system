package com.acme.usersrv.user.service;

import com.acme.usersrv.common.exception.EntityNotFoundException;
import com.acme.usersrv.common.security.CompanyUserDetails;
import com.acme.usersrv.common.utils.SecurityUtils;
import com.acme.usersrv.company.dto.CreateOwnerDto;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UpdateUserDto;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.exception.DuplicateUserException;
import com.acme.usersrv.user.mapper.UserMapper;
import com.acme.usersrv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Mono<UUID> createCompanyOwner(UUID companyId, CreateOwnerDto createOwnerDto) {
        CreateUserDto saveUserDto = userMapper.convert(createOwnerDto);
        saveUserDto.setCompanyId(companyId);
        saveUserDto.setRole(UserRole.COMPANY_OWNER);
        return createInternal(saveUserDto);
    }

    @Override
    @Transactional
    public Mono<UUID> create(CreateUserDto saveDto) {
        return SecurityUtils.hasCompanyAccess(saveDto.getCompanyId())
                .flatMap(companyId -> createInternal(saveDto));
    }

    private Mono<UUID> createInternal(CreateUserDto saveDto) {
        return duplicateCheck(saveDto)
                .map(this::mapFromDto)
                .flatMap(userRepository::save)
                .map(User::getId);
    }

    private Mono<CreateUserDto> duplicateCheck(CreateUserDto dto) {
        return existsByEmail(dto.getEmail())
                .filter(exists -> !exists)
                .map(exists -> dto)
                .switchIfEmpty(Mono.error(DuplicateUserException::new));
    }

    private User mapFromDto(CreateUserDto saveDto) {
        User newUser = userMapper.fromDto(saveDto);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setPassword(passwordEncoder.encode(saveDto.getPassword()));
        return newUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsActiveByEmail(email.toLowerCase());
    }

    @Override
    public Mono<Page<UserDto>> find(UserFilter filter, Pageable pageable) {
        return hasAccess(filter)
                .flatMap(updFilter -> userRepository.find(updFilter, pageable))
                .map(page -> page.map(userMapper::toDto));
    }

    private Mono<UserFilter> hasAccess(UserFilter filter){
        return SecurityUtils.hasCompanyAccess(filter.getCompanyId())
                .map(result -> filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDto> findById(UUID id) {
        return userRepository.findById(id)
                .flatMap(this::hasUserCompanyAccess)
                .flatMap(this::hasUserAccess)
                .map(data -> userMapper.toDto(data.getT1()))
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Mono<Void> update(UUID id, UpdateUserDto dto) {
        return userRepository.findById(id)
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .flatMap(this::hasUserCompanyAccess)
                .flatMap(this::hasUserAccess)
                .flatMap(data -> {
                    User user = data.getT1();

                    userMapper.update(user, dto);
                    if (StringUtils.isNotBlank(dto.getPassword())) {
                        user.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
                    CompanyUserDetails currentUser = data.getT2();
                    if (currentUser.hasAnyRole(UserRole.ADMIN, UserRole.COMPANY_OWNER)) {
                        user.setRole(dto.getRole());
                    }

                    return userRepository.save(user);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }

    private Mono<User> hasUserCompanyAccess(User user) {
        return SecurityUtils.hasCompanyAccess(user.getCompanyId())
                .map(id -> user);
    }

    private Mono<Tuple2<User, CompanyUserDetails>> hasUserAccess(User user) {
        return SecurityUtils.getCurrentUser()
                .filter(currentUser ->
                        currentUser.hasAnyRole(UserRole.ADMIN, UserRole.COMPANY_OWNER)
                                || user.getId().equals(currentUser.getId()))
                .map(currentUser -> Tuples.of(user, currentUser))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access denied")));
    }
}
