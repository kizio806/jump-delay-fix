package com.kizio.jumpdelayfix.common.config;

import com.kizio.jumpdelayfix.common.model.JumpProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpConfigStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldReturnDefaultsWhenConfigDoesNotExist() {
        JumpConfigStorage.LoadedConfig loadedConfig = JumpConfigStorage.load(tempDir.resolve("missing.properties"));

        assertTrue(loadedConfig.config().autoProfileSwitch());
        assertTrue(loadedConfig.serverProfiles().isEmpty());
    }

    @Test
    void shouldPersistAndReloadAutoSwitchAndServerProfiles() {
        Path configFile = tempDir.resolve("jumpdelayfix.properties");
        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);

        JumpConfigStorage.save(
                configFile,
                config,
                Map.of("server-a", JumpProfile.COMPETITIVE, "server-b", JumpProfile.STABLE)
        );

        JumpConfigStorage.LoadedConfig loadedConfig = JumpConfigStorage.load(configFile);

        assertFalse(loadedConfig.config().autoProfileSwitch());
        assertEquals(JumpProfile.COMPETITIVE, loadedConfig.serverProfiles().get("server-a"));
        assertEquals(JumpProfile.STABLE, loadedConfig.serverProfiles().get("server-b"));
    }

    @Test
    void shouldIgnoreInvalidProfileEntries() throws IOException {
        Path configFile = tempDir.resolve("jumpdelayfix.properties");
        Files.writeString(
                configFile,
                """
                autoProfileSwitch=false
                serverProfile.c2VydmVyLWE=COMPETITIVE
                serverProfile.%%%=SMART
                serverProfile.c2VydmVyLWI=NOT_A_PROFILE
                """,
                StandardCharsets.UTF_8
        );

        JumpConfigStorage.LoadedConfig loadedConfig = JumpConfigStorage.load(configFile);

        assertFalse(loadedConfig.config().autoProfileSwitch());
        assertEquals(1, loadedConfig.serverProfiles().size());
        assertEquals(JumpProfile.COMPETITIVE, loadedConfig.serverProfiles().get("server-a"));
    }
}
