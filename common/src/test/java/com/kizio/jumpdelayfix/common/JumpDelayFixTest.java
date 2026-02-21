package com.kizio.jumpdelayfix.common;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.api.ToggleFeedback;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
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
}
