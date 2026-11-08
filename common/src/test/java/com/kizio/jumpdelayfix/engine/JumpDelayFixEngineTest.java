package com.kizio.jumpdelayfix.engine;

import com.kizio.jumpdelayfix.config.ConfigManager;
import com.kizio.jumpdelayfix.feedback.ToggleFeedback;
import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpDelayFixEngineTest {

    @TempDir
    Path tempDir;

    private static final class StubJumpInput implements JumpInput {
        @Override
        public boolean isJumpPressed() {
            return false;
        }

        @Override
        public boolean isPlayerOnGround() {
            return true;
        }

        @Override
        public void jump() {
        }
    }

    @Test
    void shouldHandleLifecycleAndToggles() {
        ConfigManager configManager = new ConfigManager();
        JumpDelayFixEngine engine = new JumpDelayFixEngine(configManager, new HashMap<>());

        assertFalse(engine.isInitialized());
        engine.onClientTick(); // Safe no-op before init

        AtomicInteger feedbackCalls = new AtomicInteger();
        ToggleFeedback feedback = enabled -> feedbackCalls.incrementAndGet();
        StubJumpInput input = new StubJumpInput();

        engine.init(input, feedback, tempDir);

        assertTrue(engine.isInitialized());
        assertTrue(engine.isEnabled());
        assertNotNull(engine.getConfigManager());
        assertNotNull(engine.getJumpHandler());

        // Toggle off
        boolean toggled = engine.toggleEnabled();
        assertFalse(toggled);
        assertEquals(1, feedbackCalls.get());
        assertFalse(engine.isEnabled());

        // Toggle back on
        assertTrue(engine.toggleEnabled());
        assertEquals(2, feedbackCalls.get());
        assertTrue(engine.isEnabled());

        // Profile manipulation (manual selection disables auto switch)
        JumpProfile initial = engine.getProfile();
        JumpProfile cycled = engine.cycleProfile();
        assertEquals(initial.next(), cycled);

        engine.setProfile(JumpProfile.STABLE);
        assertEquals(JumpProfile.STABLE, engine.getProfile());

        // Manual profile selection turns auto switch off
        assertFalse(engine.isAutoProfileSwitchEnabled());
        assertTrue(engine.toggleAutoProfileSwitch());
        assertTrue(engine.isAutoProfileSwitchEnabled());
        assertFalse(engine.toggleAutoProfileSwitch());
        assertFalse(engine.isAutoProfileSwitchEnabled());

        // Tick
        engine.onClientTick();
        assertEquals(0, engine.getConfirmedJumpCount());
        assertEquals(0, engine.getRejectedJumpCount());
        assertEquals(0, engine.getInconclusiveJumpCount());

        // Transient state & disconnect
        engine.resetTransientState();
        engine.onClientDisconnect();
        assertEquals("global", engine.getActiveServerId());

        // Reset to defaults
        engine.resetToDefaults();
        assertTrue(engine.isEnabled());

        // Flush & shutdown
        engine.flushPendingConfiguration();
        engine.shutdown();
    }

    @Test
    void shouldSupportIsolatedRuntimeStateInjection() {
        com.kizio.jumpdelayfix.state.RuntimeState isolatedState =
                new com.kizio.jumpdelayfix.state.RuntimeState(false, JumpProfile.COMPETITIVE);
        JumpDelayFixEngine engine =
                new JumpDelayFixEngine(new ConfigManager(), new HashMap<>(), isolatedState);

        assertEquals(isolatedState, engine.getRuntimeState());
        assertFalse(engine.isEnabled());
        assertEquals(JumpProfile.COMPETITIVE, engine.getProfile());

        assertTrue(engine.toggleEnabled());
        assertTrue(isolatedState.isStateEnabled());
        assertEquals(JumpProfile.SMART, engine.cycleProfile());
        assertEquals(JumpProfile.SMART, isolatedState.stateProfile());
    }
}
