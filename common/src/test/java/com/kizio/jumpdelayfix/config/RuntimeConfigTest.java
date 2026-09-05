package com.kizio.jumpdelayfix.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeConfigTest {

    @Test
    void shouldSerializeAndDeserializeRuntimeConfig() {
        RuntimeConfig config = RuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);

        Properties properties = config.toProperties();
        RuntimeConfig reloaded = RuntimeConfig.fromProperties(properties);

        assertFalse(reloaded.autoProfileSwitch());
    }

    @Test
    void shouldFallbackToDefaultsWhenPropertyMissing() {
        RuntimeConfig reloaded = RuntimeConfig.fromProperties(new Properties());

        assertTrue(reloaded.autoProfileSwitch());
    }

    @Test
    void shouldIgnoreInvalidBooleanValuesAndKeepDefaults() {
        Properties properties = new Properties();
        properties.setProperty("autoProfileSwitch", "definitely-not-a-boolean");

        RuntimeConfig reloaded = RuntimeConfig.fromProperties(properties);

        assertTrue(reloaded.autoProfileSwitch());
    }
}
