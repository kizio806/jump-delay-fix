package com.kizio.jumpdelayfix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {

    @Test
    void modIdMustFollowMinecraftNamingConventions() {
        assertNotNull(Constants.MOD_ID, "MOD_ID must not be null");
        assertEquals("jumpdelayfix", Constants.MOD_ID,
                "MOD_ID must be lowercase alphanumeric to satisfy Fabric/NeoForge loader constraints");
        assertEquals(Constants.MOD_ID, Constants.MOD_ID.toLowerCase(java.util.Locale.ROOT),
                "MOD_ID must contain no uppercase characters");
    }

    @Test
    void modNameMustBeHumanReadable() {
        assertNotNull(Constants.MOD_NAME, "MOD_NAME must not be null");
        assertEquals("Jump Delay Fix", Constants.MOD_NAME,
                "MOD_NAME is used in log messages and UI; it must be human-readable");
    }
}
