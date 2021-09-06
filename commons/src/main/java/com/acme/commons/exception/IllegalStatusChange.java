package com.acme.commons.exception;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class IllegalStatusChange extends RuntimeException {
    private static final String MESSAGE = "Not allowed status change";

    public IllegalStatusChange() {
        super(MESSAGE);
    }

    public static <T> Mono<T> of() {
        return Mono.error(new IllegalStatusChange());
    }
}
