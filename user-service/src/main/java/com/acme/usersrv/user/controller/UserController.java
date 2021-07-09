package com.acme.usersrv.user.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreateUserDto createUserDto,
                                                  ServerHttpRequest request) {
        return userService.create(createUserDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }
}
