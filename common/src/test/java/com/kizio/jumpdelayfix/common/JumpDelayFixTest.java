package com.kizio.jumpdelayfix.common;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.api.ToggleFeedback;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
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
    void shouldCycleJumpProfile() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpProfile before = JumpDelayFix.getProfile();
        JumpProfile after = JumpDelayFix.cycleProfile();

        assertNotEquals(before, after);
    }

    @Test
    void shouldExportAndImportPreset() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);
        JumpDelayFix.toggleShadowMode();

        String preset = JumpDelayFix.exportCurrentPresetCode();

        JumpDelayFix.toggleShadowMode();
        assertFalse(JumpDelayFix.isShadowModeEnabled());

        assertTrue(JumpDelayFix.importPresetCode(preset));
        assertTrue(JumpDelayFix.isShadowModeEnabled());
    }

    @Test
    void shouldAllowDirectProfileSelectionAndDisableAutoSwitch() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.setProfile(JumpProfile.COMPETITIVE);

        assertEquals(JumpProfile.COMPETITIVE, JumpDelayFix.getProfile());
        assertFalse(JumpDelayFix.isAutoProfileSwitchEnabled());
    }

    @Test
    void shouldNormalizeRollbackThresholdOrdering() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.setCompetitiveRollbackRateMax(0.72D);
        JumpDelayFix.setStableRollbackRateMin(0.31D);
        JumpDelayFix.setFailsafeRollbackRate(0.42D);

        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();

        assertEquals(0.72D, config.competitiveRollbackRateMax(), 0.0001D);
        assertEquals(0.72D, config.stableRollbackRateMin(), 0.0001D);
        assertEquals(0.72D, config.failsafeRollbackRate(), 0.0001D);
    }

    @Test
    void shouldAdjustAdaptiveSettingsByDelta() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.setMinAttemptsForProfileSwitch(10);
        JumpDelayFix.setCompetitiveRollbackRateMax(0.10D);
        JumpDelayFix.setStableRollbackRateMin(0.30D);
        JumpDelayFix.setFailsafeRollbackRate(0.50D);

        assertEquals(12, JumpDelayFix.adjustMinAttemptsForProfileSwitch(2));
        assertEquals(0.12D, JumpDelayFix.adjustCompetitiveRollbackRateMax(0.02D), 0.0001D);
        assertEquals(0.33D, JumpDelayFix.adjustStableRollbackRateMin(0.03D), 0.0001D);
        assertEquals(0.54D, JumpDelayFix.adjustFailsafeRollbackRate(0.04D), 0.0001D);
    }

    @Test
    void shouldClampHudLayoutValues() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.setHudPosition(100_000, -100_000);
        JumpDelayFix.setHudScale(99.0D);

        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();

        assertEquals(10_000, config.hudOffsetX());
        assertEquals(-10_000, config.hudOffsetY());
        assertEquals(2.20D, config.hudScale(), 0.0001D);
    }

    @Test
    void shouldExportAndImportHudCustomizationInPreset() {
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.setHudPosition(123, 77);
        JumpDelayFix.setHudScale(1.35D);
        JumpDelayFix.toggleHudProfileAndPing();
        JumpDelayFix.toggleHudRollbackAndPenalty();
        JumpDelayFix.toggleHudQualityBar();

        String preset = JumpDelayFix.exportCurrentPresetCode();

        JumpDelayFix.resetSettingsToDefaults();
        assertEquals(6, JumpDelayFix.getHudOffsetX());
        assertEquals(6, JumpDelayFix.getHudOffsetY());
        assertTrue(JumpDelayFix.isHudProfileAndPingVisible());
        assertTrue(JumpDelayFix.isHudRollbackAndPenaltyVisible());
        assertTrue(JumpDelayFix.isHudQualityBarVisible());

        assertTrue(JumpDelayFix.importPresetCode(preset));
        assertEquals(123, JumpDelayFix.getHudOffsetX());
        assertEquals(77, JumpDelayFix.getHudOffsetY());
        assertEquals(1.35D, JumpDelayFix.getHudScale(), 0.0001D);
        assertFalse(JumpDelayFix.isHudProfileAndPingVisible());
        assertFalse(JumpDelayFix.isHudRollbackAndPenaltyVisible());
        assertFalse(JumpDelayFix.isHudQualityBarVisible());
    }

    @Test
    void shouldPersistPendingConfigWhenFlushed() {
        Path configFile = tempDir.resolve("jumpdelayfix.properties");
        JumpDelayFix.init(new NoOpJumpInput(), ToggleFeedback.NO_OP, tempDir);

        JumpDelayFix.toggleHudEnabled();
        assertFalse(Files.exists(configFile));

        JumpDelayFix.flushPendingConfiguration();
        assertTrue(Files.exists(configFile));
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
}
