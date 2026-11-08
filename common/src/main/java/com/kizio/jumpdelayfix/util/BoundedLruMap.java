package com.kizio.jumpdelayfix.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thread-unsafe bounded LRU map implementation based on {@link LinkedHashMap}.
 */
public final class BoundedLruMap {

    private BoundedLruMap() {
    }

    /**
     * Creates an access-ordered LRU map with a strict upper bound.
     *
     * @param maxCapacity maximum number of entries before the eldest is evicted
     * @param <K> key type
     * @param <V> value type
     * @return bounded LRU map
     */
    public static <K, V> Map<K, V> create(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be positive: " + maxCapacity);
        }
        return new LinkedHashMap<>(16, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCapacity;
            }
        };
    }
}
