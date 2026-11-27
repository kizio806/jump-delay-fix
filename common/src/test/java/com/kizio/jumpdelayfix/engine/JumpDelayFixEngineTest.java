package com.kizio.jumpdelayfix.engine;

import com.kizio.jumpdelayfix.config.ConfigManager;
import com.kizio.jumpdelayfix.feedback.ToggleFeedback;
import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
        com.kizio.jumpdelayfix.state.RuntimeState.reset();
        com.kizio.jumpdelayfix.state.RuntimeState isolatedState =
                new com.kizio.jumpdelayfix.state.RuntimeState(false, JumpProfile.COMPETITIVE);
        ConfigManager configManager = new ConfigManager();
        configManager.load(tempDir);
        JumpDelayFixEngine engine =
                new JumpDelayFixEngine(configManager, new HashMap<>(), isolatedState);

        assertEquals(isolatedState, engine.getRuntimeState());
        assertFalse(engine.isEnabled());
        assertEquals(JumpProfile.COMPETITIVE, engine.getProfile());

        assertTrue(engine.toggleEnabled());
        assertTrue(isolatedState.isStateEnabled());
        assertEquals(JumpProfile.SMART, engine.cycleProfile());
        assertEquals(JumpProfile.SMART, isolatedState.stateProfile());

        // onClientDisconnect must update isolatedState and not DEFAULT_INSTANCE
        isolatedState.setStateProfile(JumpProfile.COMPETITIVE);
        com.kizio.jumpdelayfix.state.RuntimeState.setProfile(JumpProfile.STABLE);
        engine.onClientDisconnect();
        assertEquals(JumpProfile.SMART, isolatedState.stateProfile());
        assertEquals(JumpProfile.STABLE, com.kizio.jumpdelayfix.state.RuntimeState.getProfile());

        // resetToDefaults must update isolatedState and not DEFAULT_INSTANCE
        isolatedState.setStateEnabled(false);
        isolatedState.setStateProfile(JumpProfile.COMPETITIVE);
        com.kizio.jumpdelayfix.state.RuntimeState.setEnabled(false);
        com.kizio.jumpdelayfix.state.RuntimeState.setProfile(JumpProfile.STABLE);
        engine.resetToDefaults();
        assertTrue(isolatedState.isStateEnabled());
        assertEquals(JumpProfile.SMART, isolatedState.stateProfile());
        assertFalse(com.kizio.jumpdelayfix.state.RuntimeState.isEnabled());
        assertEquals(JumpProfile.STABLE, com.kizio.jumpdelayfix.state.RuntimeState.getProfile());

        com.kizio.jumpdelayfix.state.RuntimeState.reset();
    }

    @Test
    void shouldNotPersistConfigDuringAutoProfileSwitch() {
        ConfigManager configManager = new ConfigManager();
        configManager.load(tempDir);
        configManager.setAutoProfileSwitch(true);
        configManager.flushNow();

        Map<String, com.kizio.jumpdelayfix.model.ServerAdaptiveStats> statsMap = new HashMap<>();
        JumpDelayFixEngine engine = new JumpDelayFixEngine(configManager, statsMap);
        engine.init(new StubJumpInput(), enabled -> {}, tempDir);
        assertEquals(JumpProfile.SMART, engine.getProfile());

        com.kizio.jumpdelayfix.model.ServerAdaptiveStats stats = new com.kizio.jumpdelayfix.model.ServerAdaptiveStats();
        for (int i = 0; i < 10; i++) {
            stats.update(0, 1);
        }
        statsMap.put("global", stats);

        engine.onClientTick();
        assertEquals(JumpProfile.STABLE, engine.getProfile());

        assertEquals(JumpProfile.SMART, configManager.resolveProfile("global"));
    }

    @Test
    void shouldHandleDoubleInitializationAndIdempotentShutdown() {
        ConfigManager configManager = new ConfigManager();
        JumpDelayFixEngine engine = new JumpDelayFixEngine(configManager, new HashMap<>());

        StubJumpInput input = new StubJumpInput();
        engine.init(input, enabled -> {}, tempDir);
        assertTrue(engine.isInitialized());

        // Second init should register safely and return early at shutdown hook check
        engine.init(input, enabled -> {}, tempDir);
        assertTrue(engine.isInitialized());

        engine.shutdown();
        assertTrue(engine.isInitialized());

        // Second shutdown must be a safe no-op
        engine.shutdown();
        assertTrue(engine.isInitialized());

        engine.resetForTests();
        assertFalse(engine.isInitialized());
    }

    private static final class LatencyJumpInput implements JumpInput {
        private final int latency;

        private LatencyJumpInput(int latency) {
            this.latency = latency;
        }

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

        @Override
        public int getLatencyMs() {
            return latency;
        }
    }

    @Test
    void shouldFallbackToSmartProfileWhenLatencyIsUnknownOrNegative() {
        ConfigManager configManager = new ConfigManager();
        configManager.load(tempDir);
        configManager.setAutoProfileSwitch(true);

        Map<String, com.kizio.jumpdelayfix.model.ServerAdaptiveStats> statsMap = new HashMap<>();
        JumpDelayFixEngine engine = new JumpDelayFixEngine(configManager, statsMap);
        engine.init(new LatencyJumpInput(-1), enabled -> {}, tempDir);

        com.kizio.jumpdelayfix.model.ServerAdaptiveStats stats = new com.kizio.jumpdelayfix.model.ServerAdaptiveStats();
        for (int i = 0; i < 10; i++) {
            stats.update(1, 0); // 10 confirmed jumps, 0 rollbacks
        }
        statsMap.put("global", stats);

        engine.onClientTick();
        // Since latency is -1 (unknown), it must fall back to SMART rather than COMPETITIVE
        assertEquals(JumpProfile.SMART, engine.getProfile());
    }

    @Test
    void shouldAutoSwitchToCompetitiveWhenLatencyIsLowAndRollbackIsZero() {
        ConfigManager configManager = new ConfigManager();
        configManager.load(tempDir);
        configManager.setAutoProfileSwitch(true);

        Map<String, com.kizio.jumpdelayfix.model.ServerAdaptiveStats> statsMap = new HashMap<>();
        JumpDelayFixEngine engine = new JumpDelayFixEngine(configManager, statsMap);
        engine.init(new LatencyJumpInput(20), enabled -> {}, tempDir);

        com.kizio.jumpdelayfix.model.ServerAdaptiveStats stats = new com.kizio.jumpdelayfix.model.ServerAdaptiveStats();
        for (int i = 0; i < 10; i++) {
            stats.update(1, 0); // 10 confirmed jumps, 0 rollbacks
        }
        statsMap.put("global", stats);

        engine.onClientTick();
        assertEquals(JumpProfile.COMPETITIVE, engine.getProfile());
    }
}
