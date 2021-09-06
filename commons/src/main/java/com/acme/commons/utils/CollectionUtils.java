package com.acme.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static <K, V> Properties mapToProperties(Map<K, V> map) {
        Properties properties = new Properties();
        properties.putAll(map);

        return properties;
    }

    public static <T> Set<T> asSet(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    public static <T> T getFirst(Iterable<T> items) {
        Iterator<T> iterator = items.iterator();
        return iterator.hasNext() ? items.iterator().next() : null;
    }

    public static <T> int size(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <K, V> int size(Map<K, V> map) {
        return map == null ? 0 : map.size();
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return size(collection) == 0;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return size(map) == 0;
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static String join(Collection<?> items) {
        return join(items, ",");
    }

    public static String join(Collection<?> items, String delimeter) {
        return items == null ? "" : items.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimeter));
    }

    public static <E> Collection<E> toCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ? (Collection<E>) iterable : newArrayList(iterable);
    }

    private static <E> Collection<E> newArrayList(Iterable<E> iterable) {
        List<E> items = new ArrayList<>();
        iterable.forEach(items::add);
        return items;
    }

    @SafeVarargs
    public static <E> List<E> union(Collection<E>... collections) {
        return Stream.of(collections)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
