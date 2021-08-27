package com.acme.accountingsrv.plan.exception;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class PlanAlreadyAssignedException extends RuntimeException {
    private static final String MESSAGE = "%s plan already assigned";

    public PlanAlreadyAssignedException(UUID planId) {
        super(String.format(MESSAGE, planId));
    }

    public static <T> Mono<T> of(UUID planId) {
        return Mono.error(new PlanAlreadyAssignedException(planId));
    }
}
