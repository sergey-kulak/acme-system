package com.acme.usersrv.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public interface TxStepVerifier extends StepVerifier {

    static <T> FirstStep<T> withRollback(Mono<T> publisher) {
        return StepVerifier.create(publisher.as(Transactions::withRollback));
    }

    static <T> FirstStep<T> withRollback(Flux<T> publisher) {
        return StepVerifier.create(publisher.as(Transactions::withRollback));
    }
}
