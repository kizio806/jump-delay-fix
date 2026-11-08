package com.kizio.jumpdelayfix.client.input;

import java.util.Objects;

/**
 * Shared assertion helper for verifying KeyMappings contracts across mod loaders.
 */
public final class KeyMappingAssertions {

    private KeyMappingAssertions() {
    }

    public static void assertSafeDefaults(AbstractKeyMappings mappings) {
        Objects.requireNonNull(mappings, "mappings");
        if (mappings.consumeTogglePress() || mappings.consumeProfileCyclePress() || mappings.consumeConfigScreenPress()) {
            throw new AssertionError("Key mappings must default to unpressed");
        }
    }
}
