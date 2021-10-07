package com.acme.commons.repository;

import com.acme.commons.exception.IllegalStatusChange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.acme.commons.utils.StreamUtils.mapToList;

public class RepoUtils {
    private RepoUtils() {
    }

    public static <T, I> Mono<T> link(T obj, Collection<I> ids, Function<I, Mono<Void>> linker) {
        List<Mono<?>> monos = ids == null ? List.of() : mapToList(ids, linker);
        return Mono.when(monos)
                .then(Mono.just(obj));
    }

    public static <T, S> Mono<T> isValidChange(T item,
                                                  Function<T, S> statusSupplier,
                                                  Map<S, List<S>> allowedStatusMap,
                                                  S newStatus) {
        List<S> nextStatuses =
                allowedStatusMap.getOrDefault(statusSupplier.apply(item), List.of());

        return nextStatuses.contains(newStatus) ? Mono.just(item) : IllegalStatusChange.of();
    }
}
