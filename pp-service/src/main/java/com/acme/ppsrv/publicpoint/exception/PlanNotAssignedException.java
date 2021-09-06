package com.acme.ppsrv.publicpoint.exception;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class PlanNotAssignedException extends RuntimeException {
    private static final String MESSAGE = "Plan not assigned for %s";
    private static final String COMPANY = " company";

    public PlanNotAssignedException(String company) {
        super(String.format(MESSAGE, company));
    }

    public static <T> Mono<T> of(UUID companyId) {
        return Mono.error(new PlanNotAssignedException(companyId + COMPANY));
    }

    public static <T> Mono<T> of(String companyName) {
        return Mono.error(new PlanNotAssignedException(companyName));
    }
}
