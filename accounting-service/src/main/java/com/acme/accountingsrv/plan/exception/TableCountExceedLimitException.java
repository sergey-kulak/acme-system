package com.acme.accountingsrv.plan.exception;

import reactor.core.publisher.Mono;

public class TableCountExceedLimitException extends RuntimeException {
    private static final String MESSAGE = "Current table count exceeds plan limit";

    public TableCountExceedLimitException() {
        super(MESSAGE);
    }

    public static <T> Mono<T> of() {
        return Mono.error(new TableCountExceedLimitException());
    }
}
