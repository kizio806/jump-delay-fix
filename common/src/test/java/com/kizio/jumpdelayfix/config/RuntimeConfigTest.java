package com.kizio.jumpdelayfix.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeConfigTest {

    @Test
    void shouldSerializeFalseAndRoundTripCorrectly() {
        RuntimeConfig config = RuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);

        Properties properties = config.toProperties();
        RuntimeConfig reloaded = RuntimeConfig.fromProperties(properties);

        assertFalse(reloaded.autoProfileSwitch(),
                "autoProfileSwitch=false must survive a Properties serialization round-trip");
    }

    @Test
    void shouldSerializeTrueAndRoundTripCorrectly() {
        RuntimeConfig config = RuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);
        config.setAutoProfileSwitch(true);

        Properties properties = config.toProperties();
        RuntimeConfig reloaded = RuntimeConfig.fromProperties(properties);

        assertTrue(reloaded.autoProfileSwitch(),
                "autoProfileSwitch=true must survive a Properties serialization round-trip");
    }

    @Test
    void shouldFallbackToDefaultsWhenPropertyMissing() {
        RuntimeConfig reloaded = RuntimeConfig.fromProperties(new Properties());

        assertTrue(reloaded.autoProfileSwitch(),
                "autoProfileSwitch must default to true when the property is absent");
    }

    @Test
    void shouldIgnoreInvalidBooleanValuesAndKeepDefaults() {
        Properties properties = new Properties();
        properties.setProperty("autoProfileSwitch", "definitely-not-a-boolean");

        RuntimeConfig reloaded = RuntimeConfig.fromProperties(properties);

        assertTrue(reloaded.autoProfileSwitch(),
                "Invalid boolean values must be rejected and the default (true) must be preserved");
    }

    @Test
    void copyShouldProduceIndependentInstance() {
        RuntimeConfig original = RuntimeConfig.defaults();
        original.setAutoProfileSwitch(false);

        RuntimeConfig copy = original.copy();

        assertNotSame(original, copy, "copy() must return a new instance");
        assertFalse(copy.autoProfileSwitch(), "copy() must preserve field values");

        copy.setAutoProfileSwitch(true);
        assertFalse(original.autoProfileSwitch(),
                "Mutating the copy must not affect the original");
    }

    @Test
    void defaultsMustReturnNewInstanceEachCall() {
        RuntimeConfig first = RuntimeConfig.defaults();
        RuntimeConfig second = RuntimeConfig.defaults();

        assertNotSame(first, second,
                "defaults() must return a new independent instance on each call");
        first.setAutoProfileSwitch(false);
        assertTrue(second.autoProfileSwitch(),
                "Mutating one defaults() instance must not affect another");
    }
}
