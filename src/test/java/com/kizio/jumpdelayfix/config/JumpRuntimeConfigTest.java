package com.kizio.jumpdelayfix.common.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpRuntimeConfigTest {

    @Test
    void shouldSerializeAndDeserializeRuntimeConfig() {
        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);

        Properties properties = config.toProperties();
        JumpRuntimeConfig reloaded = JumpRuntimeConfig.fromProperties(properties);

        assertFalse(reloaded.autoProfileSwitch());
    }

    @Test
    void shouldFallbackToDefaultsWhenPropertyMissing() {
        JumpRuntimeConfig reloaded = JumpRuntimeConfig.fromProperties(new Properties());

        assertTrue(reloaded.autoProfileSwitch());
    }

    @Test
    void shouldIgnoreInvalidBooleanValuesAndKeepDefaults() {
        Properties properties = new Properties();
        properties.setProperty("autoProfileSwitch", "definitely-not-a-boolean");

        JumpRuntimeConfig reloaded = JumpRuntimeConfig.fromProperties(properties);

        assertTrue(reloaded.autoProfileSwitch());
    }
}
