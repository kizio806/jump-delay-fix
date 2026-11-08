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
        assertFalse(map.containsKey("a"));
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
        assertTrue(map.containsKey("a"));
        assertFalse(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
    }
}
