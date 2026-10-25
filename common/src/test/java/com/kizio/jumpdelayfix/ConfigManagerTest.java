package com.kizio.jumpdelayfix;

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

        assertEquals(JumpProfile.SMART, manager.resolveProfile("server-x"));
        manager.rememberProfile("server-x", JumpProfile.COMPETITIVE);
        assertEquals(JumpProfile.COMPETITIVE, manager.resolveProfile("server-x"));

        manager.flushIfDue();
        manager.flushNow();
    }

    @Test
    void shouldResetToDefaults() {
        ConfigManager manager = new ConfigManager();
        manager.load(tempDir);

        manager.rememberProfile("server-y", JumpProfile.STABLE);
        manager.setAutoProfileSwitch(false);

        manager.resetToDefaults();

        assertTrue(manager.autoProfileSwitch());
        assertEquals(JumpProfile.SMART, manager.resolveProfile("server-y"));
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
}
