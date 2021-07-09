package com.acme.usersrv.test;

import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Transactions {
    private static TransactionalOperator rxTxOperator;

    public static void init(ReactiveTransactionManager transactionManager) {
        rxTxOperator = TransactionalOperator.create(transactionManager);
    }

    public static <T> Mono<T> withRollback(Mono<T> publisher) {
        return rxTxOperator.execute(tx -> {
            tx.setRollbackOnly();
            return publisher;
        }).next();
    }

    public static <T> Flux<T> withRollback(Flux<T> publisher) {
        return rxTxOperator.execute(tx -> {
            tx.setRollbackOnly();
            return publisher;
        });
    }
}
