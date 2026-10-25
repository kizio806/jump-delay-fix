package com.kizio.jumpdelayfix.config;

import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldReturnDefaultsWhenConfigDoesNotExist() {
        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(tempDir.resolve("missing.properties"));

        assertTrue(loadedConfig.config().autoProfileSwitch());
        assertTrue(loadedConfig.serverProfiles().isEmpty());
    }

    @Test
    void shouldPersistAndReloadAutoSwitchAndServerProfiles() {
        Path configFile = tempDir.resolve("jumpdelayfix.properties");
        RuntimeConfig config = RuntimeConfig.defaults();
        config.setAutoProfileSwitch(false);

        ConfigStorage.save(
                configFile,
                config,
                Map.of("server-a", JumpProfile.COMPETITIVE, "server-b", JumpProfile.STABLE)
        );

        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(configFile);

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

        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(configFile);

        assertFalse(loadedConfig.config().autoProfileSwitch());
        assertEquals(1, loadedConfig.serverProfiles().size());
        assertEquals(JumpProfile.COMPETITIVE, loadedConfig.serverProfiles().get("server-a"));
    }

    @Test
    void shouldValidateArgumentsOnSave() {
        Path validPath = tempDir.resolve("config.properties");
        RuntimeConfig validConfig = RuntimeConfig.defaults();
        Map<String, JumpProfile> validProfiles = Map.of();

        assertThrows(NullPointerException.class, () -> ConfigStorage.save(null, validConfig, validProfiles));
        assertThrows(NullPointerException.class, () -> ConfigStorage.save(validPath, null, validProfiles));
        assertThrows(NullPointerException.class, () -> ConfigStorage.save(validPath, validConfig, null));
    }

    @Test
    void shouldIgnoreNullOrBlankServerIdsWhenSaving() {
        Path configFile = tempDir.resolve("profiles.properties");
        RuntimeConfig config = RuntimeConfig.defaults();
        Map<String, JumpProfile> profiles = new HashMap<>();
        profiles.put("valid-server", JumpProfile.SMART);
        profiles.put("", JumpProfile.COMPETITIVE);
        profiles.put("   ", JumpProfile.STABLE);

        ConfigStorage.save(configFile, config, profiles);
        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(configFile);

        assertEquals(1, loaded.serverProfiles().size());
        assertEquals(JumpProfile.SMART, loaded.serverProfiles().get("valid-server"));
    }

    @Test
    void shouldFallbackToDefaultsWhenLoadFailsWithIoException() {
        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(tempDir);
        assertTrue(loaded.config().autoProfileSwitch());
        assertTrue(loaded.serverProfiles().isEmpty());
    }
}
