package com.acme.usersrv.user.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.openapi.ConflictErrorResponse;
import com.acme.usersrv.common.openapi.EntityCreatedResponse;
import com.acme.usersrv.common.openapi.ValidationErrorResponse;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User API", description = "User management API")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(description = "Create an user")
    @EntityCreatedResponse
    @ValidationErrorResponse
    @ConflictErrorResponse(description = "User with specified email already created")
    public Mono<ResponseEntity<IdDto>> create(@RequestBody CreateUserDto createUserDto,
                                              ServerHttpRequest request) {
        return userService.create(createUserDto)
                .map(id -> ResponseUtils.buildCreatedResponse(request, id));
    }
}
