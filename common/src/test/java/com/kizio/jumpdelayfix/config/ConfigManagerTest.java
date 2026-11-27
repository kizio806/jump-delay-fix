package com.kizio.jumpdelayfix.config;

import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldHandleNoOpAutoProfileSwitchUpdateWhenUnchanged() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);

        assertTrue(manager.autoProfileSwitch());
        manager.setAutoProfileSwitch(true);
        assertTrue(manager.autoProfileSwitch());

        manager.setAutoProfileSwitch(false);
        assertFalse(manager.autoProfileSwitch());
        manager.setAutoProfileSwitch(false);
        assertFalse(manager.autoProfileSwitch());
    }

    @Test
    void shouldRememberAndResolveServerProfiles() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);

        assertEquals(JumpProfile.SMART, manager.resolveProfile("server-x"),
                "Unknown server must resolve to SMART as the default profile");
        manager.rememberProfile("server-x", JumpProfile.COMPETITIVE);
        assertEquals(JumpProfile.COMPETITIVE, manager.resolveProfile("server-x"),
                "Remembered profile must be returned for the same server");

        manager.flushNow();
        ConfigStorage.LoadedConfig persisted = ConfigStorage.load(tempDir.resolve("jumpdelayfix.properties"));
        assertEquals(JumpProfile.COMPETITIVE, persisted.serverProfiles().get("server-x"),
                "flushNow must write the remembered profile to disk");
    }

    @Test
    void shouldResetToDefaults() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);

        manager.rememberProfile("server-y", JumpProfile.STABLE);
        manager.setAutoProfileSwitch(false);

        manager.resetToDefaults();

        assertTrue(manager.autoProfileSwitch(),
                "autoProfileSwitch must revert to true after resetToDefaults()");
        assertEquals(JumpProfile.SMART, manager.resolveProfile("server-y"),
                "Previously remembered server profile must be cleared after resetToDefaults()");
    }

    @Test
    void shouldHandleFailedPendingSaveGracefully() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);

        CompletableFuture<Void> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("disk error"));
        manager.setPendingSaveForTests(failed);

        assertDoesNotThrow(manager::flushNow);
    }

    @Test
    void shouldShutdownCleanly() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);
        assertDoesNotThrow(manager::shutdown);
    }

    @Test
    void shouldAllowMultipleInstancesWithIndependentExecutors() {
        ConfigManager manager1 = new ConfigManager();
        manager1.load(tempDir);
        manager1.shutdown();

        ConfigManager manager2 = new ConfigManager();
        manager2.load(tempDir.resolve("sub"));
        manager2.rememberProfile("test-server", JumpProfile.COMPETITIVE);
        manager2.flushIfDue();
        assertDoesNotThrow(manager2::flushNow);
        manager2.shutdown();
    }

    @Test
    void shouldPersistAsyncWhenDebounceCooldownExpiresInFlushIfDue() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);
        manager.rememberProfile("debounce-server", JumpProfile.COMPETITIVE);

        for (int i = 0; i < 20; i++) {
            manager.flushIfDue();
        }

        manager.flushIfDue();
        manager.flushIfDue();
        manager.shutdown();

        ConfigStorage.LoadedConfig loaded = ConfigStorage.load(tempDir.resolve("jumpdelayfix.properties"));
        assertEquals(JumpProfile.COMPETITIVE, loaded.serverProfiles().get("debounce-server"));
    }
}
