package com.acme.commons.exception;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Entity with %s not found";

    public EntityNotFoundException(UUID id) {
        super(String.format(MESSAGE, id));
    }

    public static <T> Mono<T> of(UUID id){
        return Mono.error(new EntityNotFoundException(id));
    }
}
