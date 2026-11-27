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
        profiles.put(null, JumpProfile.COMPETITIVE);
        profiles.put("", JumpProfile.COMPETITIVE);
        profiles.put("   ", JumpProfile.STABLE);
        profiles.put("null-profile-server", null);

        ConfigStorage.save(configFile, config, profiles);
        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(configFile);

        assertEquals(1, loaded.serverProfiles().size());
        assertEquals(JumpProfile.SMART, loaded.serverProfiles().get("valid-server"));
    }

    @Test
    void shouldHandleEmptyOrCorruptedServerProfileKeysDuringLoad() throws IOException {
        Path configFile = tempDir.resolve("corrupted-keys.properties");
        Files.writeString(
                configFile,
                """
                autoProfileSwitch=true
                serverProfile.=SMART
                serverProfile.   =STABLE
                serverProfile.ICAg=SMART
                serverProfile.c2VydmVyLWE=COMPETITIVE
                """,
                StandardCharsets.UTF_8
        );

        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(configFile);
        assertEquals(1, loaded.serverProfiles().size());
        assertEquals(JumpProfile.COMPETITIVE, loaded.serverProfiles().get("server-a"));
    }

    @Test
    void shouldSaveAndLoadWhenFilePathHasNoParentDirectory() throws IOException {
        Path relativePath = Path.of("jumpdelayfix-relative-test-" + System.currentTimeMillis() + ".properties");
        try {
            ConfigStorage.save(relativePath, RuntimeConfig.defaults(), Map.of("local-server", JumpProfile.STABLE));
            assertTrue(Files.exists(relativePath));

            ConfigStorage.LoadedConfig loaded = ConfigStorage.load(relativePath);
            assertEquals(JumpProfile.STABLE, loaded.serverProfiles().get("local-server"));
        } finally {
            Files.deleteIfExists(relativePath);
        }
    }

    @Test
    void shouldHandleSaveIoFailureGracefullyWithoutThrowing() throws IOException {
        Path blockingFile = tempDir.resolve("blocking-file");
        Files.writeString(blockingFile, "block");
        Path invalidTarget = blockingFile.resolve("child/config.properties");

        ConfigStorage.save(invalidTarget, RuntimeConfig.defaults(), Map.of());
        assertFalse(Files.exists(invalidTarget));
    }

    @Test
    void shouldFallbackToDefaultsWhenLoadFailsWithIoException() {
        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(tempDir);
        assertTrue(loaded.config().autoProfileSwitch());
        assertTrue(loaded.serverProfiles().isEmpty());
    }

    @Test
    void shouldLoadLegacyUnencodedServerProfiles() throws IOException {
        Path configFile = tempDir.resolve("legacy.properties");
        Files.writeString(
                configFile,
                """
                autoProfileSwitch=true
                serverProfile.mc.hypixel.net=COMPETITIVE
                serverProfile.play.example.com\\:25565=STABLE
                """,
                StandardCharsets.UTF_8
        );

        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(configFile);
        assertEquals(2, loadedConfig.serverProfiles().size());
        assertEquals(JumpProfile.COMPETITIVE, loadedConfig.serverProfiles().get("mc.hypixel.net"));
        assertEquals(JumpProfile.STABLE, loadedConfig.serverProfiles().get("play.example.com:25565"));
    }
}
