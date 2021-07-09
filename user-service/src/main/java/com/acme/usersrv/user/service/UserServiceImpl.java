package com.acme.usersrv.user.service;

import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.exception.DuplicateUserException;
import com.acme.usersrv.user.mapper.UserMapper;
import com.acme.usersrv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        return userRepository.existsByEmail(email.toLowerCase());
    }

    @Override
    public Mono<Page<User>> find(UserFilter userFilter, Pageable pageable) {
        return null;
    }
}
