package com.acme.commons.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class StreamUtils {
    private StreamUtils() {
    }

    /**
     * Returns a list consisting of the results of applying the given
     * function to the elements of this list.
     *
     * @param list   the original elements
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @param <T>    The element type of the old list
     * @param <R>    The element type of the new list
     * @return a list consisting of the results of applying the given
     * function to the elements of this list.
     */
    public static <T, R> List<R> mapToList(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(toList());
    }

    /**
     * Returns a set consisting of the results of applying the given
     * function to the elements of this list.
     *
     * @param list   the original elements
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @param <T>    The element type of the old list
     * @param <R>    The element type of the new set
     * @return a list consisting of the results of applying the given
     * function to the elements of this list.
     */
    public static <T, R> Set<R> mapToSet(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(toSet());
    }

    /**
     * Wrapper around {@link Collectors#groupingBy(Function)}
     *
     * @param src        collection to transform
     * @param classifier the classifier function mapping input elements to keys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, List<V>> groupToLists(Collection<V> src, Function<? super V, ? extends K> classifier) {
        return src.stream().collect(groupingBy(classifier));
    }

    public static <K, V, NV> Map<K, List<NV>> groupToListsAndMap(Collection<V> src,
                                                                 Function<? super V, ? extends K> classifier,
                                                                 Function<? super V, ? extends NV> mapper) {
        return src.stream().collect(groupingBy(classifier,
                Collectors.mapping(mapper, Collectors.toList())));
    }

    /**
     * Wrapper around {@link Collectors#groupingBy(Function)}
     *
     * @param src        collection to transform
     * @param classifier the classifier function mapping input elements to keys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, Set<V>> groupToSets(Collection<V> src, Function<? super V, ? extends K> classifier) {
        return src.stream().collect(groupingBy(classifier, toSet()));
    }

    /**
     * This is wrapper upon Stream.collect(Collectors.toMap) function.
     *
     * @param src         collection to transform
     * @param <T>         the type of the input elements
     * @param <K>         the output type of the key mapping function
     * @param <U>         the output type of the value mapping function
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a map that will be constructed by {@code keyMapper} and {@code valueMapper}.
     */
    public static <T, K, U> Map<K, U> mapToMap(Collection<T> src,
                                               Function<? super T, ? extends K> keyMapper,
                                               Function<? super T, ? extends U> valueMapper) {
        return src.stream()
                .collect(HashMap::new, (m, v) -> m.put(keyMapper.apply(v), valueMapper.apply(v)), HashMap::putAll);
    }

    public static <T> List<T> filter(Collection<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> T findFirst(Collection<T> collection) {
        return collection.stream().findFirst().orElse(null);
    }

    public static <T, R> R findFirstAndMap(Collection<T> collection, Function<? super T, ? extends R> mapFunctoin) {
        return collection.stream().findFirst().map(mapFunctoin).orElse(null);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <K, V, NK, NV> Map<NK, NV> mapToMap(Map<K, V> src,
                                                      Function<? super K, ? extends NK> keyMapper,
                                                      Function<? super V, ? extends NV> valueMapper) {
        return mapToMap(src, keyMapper, valueMapper, false);
    }

    public static <K, V, NK, NV> Map<NK, NV> mapToMap(Map<K, V> src,
                                                      Function<? super K, ? extends NK> keyMapper,
                                                      Function<? super V, ? extends NV> valueMapper, boolean savePosition) {
        return src.entrySet().stream()
                .collect(savePosition ? LinkedHashMap<NK, NV>::new : HashMap<NK, NV>::new,
                        (m, e) -> m.put(keyMapper.apply(e.getKey()), valueMapper.apply(e.getValue())), HashMap::putAll);
    }
}
