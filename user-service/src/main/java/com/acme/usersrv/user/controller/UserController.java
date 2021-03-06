package com.acme.usersrv.user.controller;

import com.acme.usersrv.common.dto.IdDto;
import com.acme.usersrv.common.openapi.ConflictErrorResponse;
import com.acme.usersrv.common.openapi.EntityCreatedResponse;
import com.acme.usersrv.common.openapi.EntityNotFoundResponse;
import com.acme.usersrv.common.openapi.OpenApiPage;
import com.acme.usersrv.common.openapi.ValidationErrorResponse;
import com.acme.usersrv.common.utils.ResponseUtils;
import com.acme.usersrv.user.dto.CreateUserDto;
import com.acme.usersrv.user.dto.UpdateUserDto;
import com.acme.usersrv.user.dto.UserDto;
import com.acme.usersrv.user.dto.UserFilter;
import com.acme.usersrv.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

    @GetMapping("{id}")
    @Operation(description = "Find user by id")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<UserDto> findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(description = "Update user")
    @ApiResponse(responseCode = "200")
    @EntityNotFoundResponse
    public Mono<Void> update(@PathVariable UUID id, @RequestBody UpdateUserDto dto) {
        return userService.update(id, dto);
    }

    @GetMapping
    @Operation(description = "Find users with pagination")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = UserApiPage.class)))
    public Mono<Page<UserDto>> find(@ParameterObject UserFilter filter,
                                    @ParameterObject Pageable pageable) {
        return userService.find(filter, pageable);
    }

    @Schema(name = "User page")
    private static class UserApiPage extends OpenApiPage<UserDto> {
    }
}
