package com.kizio.jumpdelayfix.common;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.api.ToggleFeedback;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpDelayFixTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        JumpDelayFix.resetForTests();
    }

    @Test
    void shouldToggleAndNotifyFeedback() {
        AtomicInteger feedbackCalls = new AtomicInteger();

        JumpDelayFix.init(new NoOpJumpInput(), enabled -> feedbackCalls.incrementAndGet(), tempDir);

        boolean enabledAfterToggle = JumpDelayFix.toggleEnabled();

        assertEquals(1, feedbackCalls.get());
        assertFalse(enabledAfterToggle);
    }

    @Test
    void shouldTickWithoutAssetsOrMinecraftClasses() {
        AtomicInteger jumpCalls = new AtomicInteger();

        JumpDelayFix.init(new CountingJumpInput(jumpCalls), ToggleFeedback.NO_OP, tempDir);
        JumpDelayFix.onClientTick();

        assertEquals(1, jumpCalls.get());
    }

    @Test
    void shouldCycleJumpProfileAndDisableAutoSwitch() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpProfile before = JumpDelayFix.getProfile();
        JumpProfile after = JumpDelayFix.cycleProfile();

        assertNotEquals(before, after);
        assertFalse(JumpDelayFix.isAutoProfileSwitchEnabled());
    }

    @Test
    void shouldPersistAutoSwitchSettingAcrossReloads() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        assertFalse(JumpDelayFix.toggleAutoProfileSwitch());
        JumpDelayFix.flushPendingConfiguration();

        JumpDelayFix.resetForTests();
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        assertFalse(JumpDelayFix.isAutoProfileSwitchEnabled());
    }

    @Test
    void shouldRememberManualProfilePerServer() {
        MutableServerJumpInput input = new MutableServerJumpInput();
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        input.serverId = "server-a";
        JumpDelayFix.onClientTick();
        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        input.serverId = "server-b";
        JumpDelayFix.onClientTick();
        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());

        JumpDelayFix.setProfile(JumpProfile.STABLE);
        input.serverId = "server-a";
        JumpDelayFix.onClientTick();

        assertEquals(JumpProfile.COMPETITIVE, JumpDelayFix.getProfile());
    }

    @Test
    void shouldAutoSwitchToStableOnHighLatencyServer() {
        AdaptiveJumpInput input = new AdaptiveJumpInput("high-ping", 220);
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        runTicks(input, 24);

        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
        assertEquals(JumpProfile.STABLE, JumpDelayFix.getProfile());
    }

    @Test
    void shouldAutoSwitchToCompetitiveOnLowLatencyServer() {
        AdaptiveJumpInput input = new AdaptiveJumpInput("low-ping", 40);
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        runTicks(input, 24);

        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
        assertEquals(JumpProfile.COMPETITIVE, JumpDelayFix.getProfile());
    }

    @Test
    void shouldCapPerServerProfileMemorySize() throws IOException {
        MutableServerJumpInput input = new MutableServerJumpInput();
        Path configFile = tempDir.resolve("jumpdelayfix.properties");
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        for (int index = 0; index < 200; index++) {
            input.serverId = "server-" + index;
            JumpDelayFix.onClientTick();
            JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);
        }

        JumpDelayFix.flushPendingConfiguration();

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(configFile)) {
            properties.load(inputStream);
        }

        long storedProfiles = properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("serverProfile."))
                .count();

        assertTrue(storedProfiles <= 128, "Expected <= 128 server profiles, got " + storedProfiles);
    }

    private static void runTicks(AdaptiveJumpInput input, int count) {
        for (int index = 0; index < count; index++) {
            input.advanceTick();
            JumpDelayFix.onClientTick();
        }
    }

    private static final class NoOpJumpInput implements JumpInput {

        @Override
        public boolean isJumpPressed() {
            return false;
        }

        @Override
        public boolean isPlayerOnGround() {
            return false;
        }

        @Override
        public void jump() {
        }
    }

    private static final class CountingJumpInput implements JumpInput {

        private final AtomicInteger jumpCalls;

        private CountingJumpInput(AtomicInteger jumpCalls) {
            this.jumpCalls = jumpCalls;
        }

        @Override
        public boolean isJumpPressed() {
            return true;
        }

        @Override
        public boolean isPlayerOnGround() {
            return true;
        }

        @Override
        public void jump() {
            jumpCalls.incrementAndGet();
        }
    }

    private static final class MutableServerJumpInput implements JumpInput {

        private String serverId = "global";

        @Override
        public boolean isJumpPressed() {
            return false;
        }

        @Override
        public boolean isPlayerOnGround() {
            return false;
        }

        @Override
        public void jump() {
        }

        @Override
        public String getServerIdentifier() {
            return serverId;
        }
    }

    private static final class AdaptiveJumpInput implements JumpInput {

        private final String serverId;
        private final int latencyMs;
        private boolean riseNextTick;
        private boolean landNextTick;
        private boolean onGround = true;
        private double playerY = 64.0D;

        private AdaptiveJumpInput(String serverId, int latencyMs) {
            this.serverId = serverId;
            this.latencyMs = latencyMs;
        }

        private void advanceTick() {
            if (riseNextTick) {
                playerY = 65.0D;
                onGround = false;
                riseNextTick = false;
                landNextTick = true;
                return;
            }

            if (landNextTick) {
                playerY = 64.0D;
                onGround = true;
                landNextTick = false;
                return;
            }

            playerY = 64.0D;
            onGround = true;
        }

        @Override
        public boolean isJumpPressed() {
            return true;
        }

        @Override
        public boolean isPlayerOnGround() {
            return onGround;
        }

        @Override
        public void jump() {
            riseNextTick = true;
        }

        @Override
        public double getPlayerY() {
            return playerY;
        }

        @Override
        public int getLatencyMs() {
            return latencyMs;
        }

        @Override
        public String getServerIdentifier() {
            return serverId;
        }
    }
}
