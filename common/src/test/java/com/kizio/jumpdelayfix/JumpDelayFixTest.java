package com.kizio.jumpdelayfix;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.feedback.ToggleFeedback;
import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

        runTicks(input, 48);

        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
        assertEquals(JumpProfile.STABLE, JumpDelayFix.getProfile());
    }

    @Test
    void shouldAutoSwitchToCompetitiveOnLowLatencyServer() {
        AdaptiveJumpInput input = new AdaptiveJumpInput("low-ping", 40);
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        runTicks(input, 48);

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

    @Test
    void shouldResetActiveSessionToGlobalOnDisconnect() {
        MutableServerJumpInput input = new MutableServerJumpInput();
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        input.serverId = "server-a";
        JumpDelayFix.onClientTick();
        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        JumpDelayFix.onClientDisconnect();

        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());
    }

    @Test
    void shouldResetToDefaultSettings() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);
        JumpDelayFix.setProfile(JumpProfile.STABLE);
        JumpDelayFix.flushPendingConfiguration();

        JumpDelayFix.resetToDefaults();

        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());
        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
        assertTrue(JumpDelayFix.isEnabled());
        assertEquals(0, JumpDelayFix.getInconclusiveJumpCount());
    }

    @Test
    void shouldResetToDefaultsPreservingActiveServerContext() {
        MutableServerJumpInput input = new MutableServerJumpInput();
        input.serverId = "mc.example.com";
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);
        JumpDelayFix.onClientTick();

        assertEquals("mc.example.com", JumpDelayFix.getActiveServerId());
        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        JumpDelayFix.resetToDefaults();

        assertEquals("mc.example.com", JumpDelayFix.getActiveServerId());
        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());
        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
    }

    @Test
    void shouldResetTransientStateWithoutClearingConfiguration() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);
        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        JumpDelayFix.resetTransientState();

        assertEquals(JumpProfile.COMPETITIVE, JumpDelayFix.getProfile());
    }

    @Test
    void shouldSafelyIgnoreTickWhenNotInitialized() {
        assertDoesNotThrow(JumpDelayFix::onClientTick);
    }

    @Test
    void shouldNotAlterServerStatsOrSwitchProfileOnInconclusiveTimeout() {
        SimulatedTimeoutJumpInput input = new SimulatedTimeoutJumpInput();
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.onClientTick();
        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());

        input.jumpPressed = true;
        input.onGround = true;
        input.playerY = 64.0D;
        JumpDelayFix.onClientTick();

        input.onGround = false;
        input.playerY = 70.0D;
        for (int i = 0; i < 25; i++) {
            JumpDelayFix.onClientTick();
        }

        assertEquals(1, JumpDelayFix.getInconclusiveJumpCount());
        assertEquals(0, JumpDelayFix.getConfirmedJumpCount());
        assertEquals(0, JumpDelayFix.getRejectedJumpCount());
        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());
    }

    @Test
    void shouldInitializeWithDefaultConfigDirectory() {
        assertDoesNotThrow(() -> JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP));
        assertTrue(JumpDelayFix.isEnabled());
    }

    @Test
    void shouldAutoSwitchToSmartOnMediumLatencyServer() {
        AdaptiveJumpInput input = new AdaptiveJumpInput("medium-ping", 110);
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        runTicks(input, 48);

        assertTrue(JumpDelayFix.isAutoProfileSwitchEnabled());
        assertEquals(JumpProfile.SMART, JumpDelayFix.getProfile());
    }

    @Test
    void shouldFallbackToDefaultServerWhenServerIdIsBlankOrNull() {
        MutableServerJumpInput input = new MutableServerJumpInput();
        JumpDelayFix.init(input, ToggleFeedback.NO_OP, tempDir);

        input.serverId = "   ";
        JumpDelayFix.onClientTick();
        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        input.serverId = null;
        JumpDelayFix.onClientTick();
        assertEquals(JumpProfile.COMPETITIVE, JumpDelayFix.getProfile());
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
        private boolean sustainAirNextTick;
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
                sustainAirNextTick = true;
                return;
            }

            if (sustainAirNextTick) {
                playerY = 65.0D;
                onGround = false;
                sustainAirNextTick = false;
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

    private static final class SimulatedTimeoutJumpInput implements JumpInput {
        private String serverId = "pvp-server";
        private boolean jumpPressed = true;
        private boolean onGround = true;
        private double playerY = 64.0D;

        @Override
        public boolean isJumpPressed() {
            return jumpPressed;
        }

        @Override
        public boolean isPlayerOnGround() {
            return onGround;
        }

        @Override
        public void jump() {
        }

        @Override
        public double getPlayerY() {
            return playerY;
        }

        @Override
        public String getServerIdentifier() {
            return serverId;
        }
    }
}
