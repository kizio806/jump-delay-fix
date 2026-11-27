package com.kizio.jumpdelayfix.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedLruMapTest {

    @Test
    void shouldThrowWhenCapacityIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> BoundedLruMap.create(0));
        assertThrows(IllegalArgumentException.class, () -> BoundedLruMap.create(-1));
    }

    @Test
    void shouldEvictEldestEntryWhenCapacityExceeded() {
        Map<String, Integer> map = BoundedLruMap.create(2);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        assertEquals(2, map.size());
        assertFalse(map.containsKey("a"), "Eldest entry 'a' must be evicted when capacity is exceeded");
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
    }

    @Test
    void shouldUpdateAccessOrderOnRead() {
        Map<String, Integer> map = BoundedLruMap.create(2);
        map.put("a", 1);
        map.put("b", 2);

        // Access "a" so "b" becomes the eldest
        assertEquals(1, map.get("a"));

        map.put("c", 3);

        assertEquals(2, map.size());
        assertTrue(map.containsKey("a"), "Recently accessed 'a' must survive eviction");
        assertFalse(map.containsKey("b"), "Least-recently-accessed 'b' must be evicted");
        assertTrue(map.containsKey("c"));
    }

    @Test
    void shouldNotEvictWhenAtExactCapacity() {
        Map<String, Integer> map = BoundedLruMap.create(3);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        assertEquals(3, map.size(), "Map must retain all entries when at exactly its capacity");
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
    }

    @Test
    void shouldSupportCapacityOfOne() {
        Map<String, Integer> map = BoundedLruMap.create(1);
        map.put("a", 1);
        assertEquals(1, map.size());

        map.put("b", 2);

        assertEquals(1, map.size(), "Capacity-1 map must hold exactly one entry");
        assertFalse(map.containsKey("a"), "'a' must be evicted when 'b' is inserted into a capacity-1 map");
        assertTrue(map.containsKey("b"));
    }

    @Test
    void reputtingSameKeyMustNotTriggerEviction() {
        Map<String, Integer> map = BoundedLruMap.create(2);
        map.put("a", 1);
        map.put("b", 2);

        // Re-put "a" with a new value — this must NOT evict anything
        map.put("a", 99);

        assertEquals(2, map.size(), "Re-putting an existing key must not evict other entries");
        assertEquals(99, map.get("a"), "Re-put must update the value");
        assertTrue(map.containsKey("b"), "'b' must survive a re-put of 'a'");
    }

    @Test
    void shouldEvictCorrectlyUnderHighInsertionLoad() {
        int capacity = 5;
        Map<String, Integer> map = BoundedLruMap.create(capacity);
        for (int i = 0; i < 20; i++) {
            map.put("key-" + i, i);
        }

        assertEquals(capacity, map.size(), "Map must maintain exactly its capacity after bulk insertions");

        // Last 5 inserted (keys 15–19) must be present
        for (int i = 15; i < 20; i++) {
            assertTrue(map.containsKey("key-" + i), "key-" + i + " must be in the map");
        }
        // First 15 inserted must have been evicted
        for (int i = 0; i < 15; i++) {
            assertFalse(map.containsKey("key-" + i), "key-" + i + " must have been evicted");
        }
    }
}
