package com.acme.commons.repository;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.acme.commons.utils.StreamUtils.mapToList;

public class RepoUtils {
    private RepoUtils() {
    }

    public static <T, I> Mono<T> link(T obj, Collection<I> ids, Function<I, Mono<Void>> linker) {
        List<Mono<?>> monos = ids == null ? List.of() : mapToList(ids, linker);
        return Mono.when(monos)
                .then(Mono.just(obj));
    }
}
