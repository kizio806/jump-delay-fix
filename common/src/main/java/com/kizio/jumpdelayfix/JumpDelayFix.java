package com.kizio.jumpdelayfix;

import com.kizio.jumpdelayfix.config.ConfigManager;
import com.kizio.jumpdelayfix.engine.JumpDelayFixEngine;
import com.kizio.jumpdelayfix.feedback.ToggleFeedback;
import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.jump.JumpHandler;
import com.kizio.jumpdelayfix.model.JumpProfile;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Static façade delegating to a default {@link JumpDelayFixEngine} instance.
 * Preserves backwards compatibility for loader entrypoints and Mixins.
 */
public final class JumpDelayFix {

    private static final JumpDelayFixEngine DEFAULT_ENGINE = new JumpDelayFixEngine();

    private JumpDelayFix() {
    }

    public static JumpDelayFixEngine getEngine() {
        return DEFAULT_ENGINE;
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback) {
        DEFAULT_ENGINE.init(input, feedback);
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback, Path configDirectory) {
        DEFAULT_ENGINE.init(input, feedback, configDirectory);
    }

    public static synchronized void onClientTick() {
        DEFAULT_ENGINE.onClientTick();
    }

    public static synchronized boolean toggleEnabled() {
        return DEFAULT_ENGINE.toggleEnabled();
    }

    public static synchronized boolean isEnabled() {
        return DEFAULT_ENGINE.isEnabled();
    }

    public static synchronized boolean isInitialized() {
        return DEFAULT_ENGINE.isInitialized();
    }

    public static synchronized JumpProfile getProfile() {
        return DEFAULT_ENGINE.getProfile();
    }

    public static synchronized JumpProfile cycleProfile() {
        return DEFAULT_ENGINE.cycleProfile();
    }

    public static synchronized JumpProfile setProfile(JumpProfile profile) {
        return DEFAULT_ENGINE.setProfile(profile);
    }

    public static synchronized boolean toggleAutoProfileSwitch() {
        return DEFAULT_ENGINE.toggleAutoProfileSwitch();
    }

    public static synchronized boolean isAutoProfileSwitchEnabled() {
        return DEFAULT_ENGINE.isAutoProfileSwitchEnabled();
    }

    public static synchronized void flushPendingConfiguration() {
        DEFAULT_ENGINE.flushPendingConfiguration();
    }

    public static synchronized void resetToDefaults() {
        DEFAULT_ENGINE.resetToDefaults();
    }

    public static synchronized void resetTransientState() {
        DEFAULT_ENGINE.resetTransientState();
    }

    public static synchronized String getActiveServerId() {
        return DEFAULT_ENGINE.getActiveServerId();
    }

    public static synchronized int getConfirmedJumpCount() {
        return DEFAULT_ENGINE.getConfirmedJumpCount();
    }

    public static synchronized int getRejectedJumpCount() {
        return DEFAULT_ENGINE.getRejectedJumpCount();
    }

    public static synchronized int getInconclusiveJumpCount() {
        return DEFAULT_ENGINE.getInconclusiveJumpCount();
    }

    public static synchronized void onClientDisconnect() {
        DEFAULT_ENGINE.onClientDisconnect();
    }

    public static synchronized void shutdown() {
        DEFAULT_ENGINE.shutdown();
    }

    public static synchronized ConfigManager getConfigManager() {
        return DEFAULT_ENGINE.getConfigManager();
    }

    public static synchronized JumpHandler getJumpHandler() {
        return DEFAULT_ENGINE.getJumpHandler();
    }

    static synchronized void resetForTests() {
        DEFAULT_ENGINE.resetForTests();
    }
}
