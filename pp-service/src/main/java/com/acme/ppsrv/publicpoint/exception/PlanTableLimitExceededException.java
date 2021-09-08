package com.acme.ppsrv.publicpoint.exception;

import reactor.core.publisher.Mono;

public class PlanTableLimitExceededException extends RuntimeException {
    private static final String MESSAGE = "Plan table limit exceeded";

    public PlanTableLimitExceededException() {
        super(MESSAGE);
    }

    public static <T> Mono<T> of() {
        return Mono.error(new PlanTableLimitExceededException());
    }
}
