package com.acme.usersrv.user.service;

import com.acme.usersrv.common.exception.EntityNotFoundException;
import com.acme.usersrv.user.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Mono<UUID> create(CreateUserDto saveDto) {
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
        return userRepository.find(filter, pageable)
                .map(page -> page.map(userMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDto> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .switchIfEmpty(EntityNotFoundException.of(id));
    }

    @Override
    public Mono<Void> update(UUID id, UpdateUserDto dto) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    userMapper.update(user, dto);
                    if (StringUtils.isNotBlank(dto.getPassword())) {
                        user.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
                    // TODO admin and company owner can update role
                    return userRepository.save(user);
                })
                .switchIfEmpty(EntityNotFoundException.of(id))
                .then();
    }
}
