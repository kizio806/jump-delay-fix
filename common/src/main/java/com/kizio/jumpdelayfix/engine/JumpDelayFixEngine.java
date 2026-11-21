package com.kizio.jumpdelayfix.engine;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.config.ConfigManager;
import com.kizio.jumpdelayfix.feedback.ToggleFeedback;
import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.jump.JumpHandler;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.model.ServerAdaptiveStats;
import com.kizio.jumpdelayfix.state.RuntimeState;
import com.kizio.jumpdelayfix.util.BoundedLruMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Instantiable, thread-safe core engine for Jump Delay Fix.
 */
public final class JumpDelayFixEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    private static final String DEFAULT_SERVER_ID = "global";
    private static final int AUTO_SWITCH_COOLDOWN_TICKS = 40;
    private static final int AUTO_SWITCH_MIN_ATTEMPTS = 8;
    private static final int STABLE_PROFILE_MIN_LATENCY_MS = 210;
    private static final int COMPETITIVE_PROFILE_MAX_LATENCY_MS = 95;
    private static final double COMPETITIVE_PROFILE_MAX_ROLLBACK_RATE = 0.08D;
    private static final double STABLE_PROFILE_MIN_ROLLBACK_RATE = 0.30D;
    private static final int MAX_TRACKED_SERVERS = 128;

    private final ConfigManager configManager;
    private final Map<String, ServerAdaptiveStats> serverStats;

    private JumpHandler jumpHandler = JumpHandler.noOp();
    private ToggleFeedback toggleFeedback = ToggleFeedback.NO_OP;
    private boolean initialized;
    private boolean shutdownHookRegistered;
    private String activeServerId = DEFAULT_SERVER_ID;
    private int lastConfirmedJumps;
    private int lastRejectedJumps;
    private int profileSwitchCooldownTicks;

    public JumpDelayFixEngine() {
        this(new ConfigManager(), BoundedLruMap.create(MAX_TRACKED_SERVERS));
    }

    public JumpDelayFixEngine(ConfigManager configManager, Map<String, ServerAdaptiveStats> serverStats) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.serverStats = Objects.requireNonNull(serverStats, "serverStats");
    }

    public synchronized void init(JumpInput input, ToggleFeedback feedback) {
        init(input, feedback, Path.of("config"));
    }

    public synchronized void init(JumpInput input, ToggleFeedback feedback, Path configDirectory) {
        this.jumpHandler = new JumpHandler(Objects.requireNonNull(input, "input"));
        this.toggleFeedback = Objects.requireNonNull(feedback, "feedback");

        registerShutdownHookIfNeeded();
        configManager.load(Objects.requireNonNull(configDirectory, "configDirectory"));

        serverStats.clear();
        activeServerId = DEFAULT_SERVER_ID;
        RuntimeState.setEnabled(true);
        RuntimeState.setProfile(resolveProfileForServer(activeServerId));

        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
        profileSwitchCooldownTicks = 0;
        initialized = true;

        LOGGER.info("{} initialized", Constants.MOD_NAME);
    }

    public synchronized void onClientTick() {
        if (!initialized) {
            return;
        }

        synchronizeServerContext();
        jumpHandler.tick();
        updateServerStats();

        if (configManager.autoProfileSwitch()) {
            applyAutoProfileSwitch();
        }

        if (profileSwitchCooldownTicks > 0) {
            profileSwitchCooldownTicks--;
        }

        configManager.flushIfDue();
    }

    public synchronized boolean toggleEnabled() {
        boolean enabled = RuntimeState.toggle();
        if (!enabled) {
            jumpHandler.resetTransientState();
        }
        toggleFeedback.onToggle(enabled);

        LOGGER.debug("{} toggled {}", Constants.MOD_NAME, enabled ? "on" : "off");
        return enabled;
    }

    public synchronized boolean isEnabled() {
        return RuntimeState.isEnabled();
    }

    public synchronized boolean isInitialized() {
        return initialized;
    }

    public synchronized JumpProfile getProfile() {
        return RuntimeState.getProfile();
    }

    public synchronized JumpProfile cycleProfile() {
        JumpProfile profile = RuntimeState.cycleProfile();
        rememberManualProfileSelection(profile);

        LOGGER.debug("{} profile {}", Constants.MOD_NAME, profile.name());
        return profile;
    }

    public synchronized JumpProfile setProfile(JumpProfile profile) {
        JumpProfile resolvedProfile = Objects.requireNonNull(profile, "profile");
        RuntimeState.setProfile(resolvedProfile);
        rememberManualProfileSelection(resolvedProfile);

        LOGGER.debug("{} profile {}", Constants.MOD_NAME, resolvedProfile.name());
        return resolvedProfile;
    }

    public synchronized boolean toggleAutoProfileSwitch() {
        boolean enabled = !configManager.autoProfileSwitch();
        configManager.setAutoProfileSwitch(enabled);
        profileSwitchCooldownTicks = 0;
        return enabled;
    }

    public synchronized boolean isAutoProfileSwitchEnabled() {
        return configManager.autoProfileSwitch();
    }

    public synchronized void flushPendingConfiguration() {
        configManager.flushNow();
    }

    public synchronized void resetToDefaults() {
        configManager.resetToDefaults();
        serverStats.clear();
        activeServerId = normalizeServerId(jumpHandler.getServerIdentifier());
        RuntimeState.setEnabled(true);
        RuntimeState.setProfile(resolveProfileForServer(activeServerId));
        lastConfirmedJumps = jumpHandler.getConfirmedJumpCount();
        lastRejectedJumps = jumpHandler.getRejectedJumpCount();
        profileSwitchCooldownTicks = 0;
        jumpHandler.resetTransientState();
        LOGGER.info("{} reset to default configuration", Constants.MOD_NAME);
    }

    public synchronized void resetTransientState() {
        jumpHandler.resetTransientState();
        profileSwitchCooldownTicks = 0;
    }

    public synchronized String getActiveServerId() {
        return activeServerId;
    }

    public synchronized int getConfirmedJumpCount() {
        return jumpHandler.getConfirmedJumpCount();
    }

    public synchronized int getRejectedJumpCount() {
        return jumpHandler.getRejectedJumpCount();
    }

    public synchronized int getInconclusiveJumpCount() {
        return jumpHandler.getInconclusiveJumpCount();
    }

    public synchronized void onClientDisconnect() {
        configManager.flushNow();
        activeServerId = DEFAULT_SERVER_ID;
        jumpHandler.resetTransientState();
        lastConfirmedJumps = jumpHandler.getConfirmedJumpCount();
        lastRejectedJumps = jumpHandler.getRejectedJumpCount();
        profileSwitchCooldownTicks = 0;
        RuntimeState.setProfile(resolveProfileForServer(activeServerId));
    }

    public synchronized void shutdown() {
        configManager.shutdown();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public JumpHandler getJumpHandler() {
        return jumpHandler;
    }

    private void rememberManualProfileSelection(JumpProfile profile) {
        rememberProfileForActiveServer(profile);

        if (configManager.autoProfileSwitch()) {
            configManager.setAutoProfileSwitch(false);
        }

        profileSwitchCooldownTicks = 0;
    }

    private void synchronizeServerContext() {
        String rawServerId = jumpHandler.getServerIdentifier();
        if (Objects.equals(rawServerId, activeServerId)) {
            return;
        }

        String serverId = normalizeServerId(rawServerId);
        if (Objects.equals(serverId, activeServerId)) {
            return;
        }

        activeServerId = serverId;
        jumpHandler.resetTransientState();
        RuntimeState.setProfile(resolveProfileForServer(activeServerId));
        lastConfirmedJumps = jumpHandler.getConfirmedJumpCount();
        lastRejectedJumps = jumpHandler.getRejectedJumpCount();
    }

    private void updateServerStats() {
        int confirmed = jumpHandler.getConfirmedJumpCount();
        int rejected = jumpHandler.getRejectedJumpCount();

        int confirmedDelta = Math.max(0, confirmed - lastConfirmedJumps);
        int rejectedDelta = Math.max(0, rejected - lastRejectedJumps);

        lastConfirmedJumps = confirmed;
        lastRejectedJumps = rejected;

        ServerAdaptiveStats stats = serverStats.computeIfAbsent(activeServerId, ignored -> new ServerAdaptiveStats());
        stats.update(confirmedDelta, rejectedDelta);
    }

    private void applyAutoProfileSwitch() {
        if (profileSwitchCooldownTicks > 0) {
            return;
        }

        ServerAdaptiveStats stats = serverStats.get(activeServerId);
        if (stats == null) {
            return;
        }

        int attempts = stats.confirmedJumps() + stats.rejectedJumps();
        if (attempts < AUTO_SWITCH_MIN_ATTEMPTS) {
            return;
        }

        JumpProfile targetProfile = chooseAutoProfile(jumpHandler.getLatencyMs(), stats.rollbackRate());
        JumpProfile currentProfile = RuntimeState.getProfile();
        if (targetProfile == currentProfile) {
            return;
        }

        RuntimeState.setProfile(targetProfile);
        rememberProfileForActiveServer(targetProfile);
        profileSwitchCooldownTicks = AUTO_SWITCH_COOLDOWN_TICKS;

        LOGGER.debug(
                "Auto-switched profile to {} (server={}, ping={}ms, rollbackRate={})",
                targetProfile.name(),
                activeServerId,
                jumpHandler.getLatencyMs(),
                stats.rollbackRate()
        );
    }

    private static JumpProfile chooseAutoProfile(int latencyMs, double rollbackRate) {
        if (rollbackRate >= STABLE_PROFILE_MIN_ROLLBACK_RATE || latencyMs >= STABLE_PROFILE_MIN_LATENCY_MS) {
            return JumpProfile.STABLE;
        }

        if (rollbackRate <= COMPETITIVE_PROFILE_MAX_ROLLBACK_RATE
                && latencyMs >= 0
                && latencyMs <= COMPETITIVE_PROFILE_MAX_LATENCY_MS) {
            return JumpProfile.COMPETITIVE;
        }

        return JumpProfile.SMART;
    }

    private static String normalizeServerId(String rawServerId) {
        if (rawServerId == null || rawServerId.isBlank()) {
            return DEFAULT_SERVER_ID;
        }
        return rawServerId.trim().toLowerCase(Locale.ROOT);
    }

    private JumpProfile resolveProfileForServer(String serverId) {
        return configManager.resolveProfile(serverId);
    }

    private void rememberProfileForActiveServer(JumpProfile profile) {
        configManager.rememberProfile(activeServerId, profile);
    }

    private void registerShutdownHookIfNeeded() {
        if (shutdownHookRegistered) {
            return;
        }

        Thread hook = new Thread(() -> {
            try {
                flushPendingConfiguration();
            } catch (RuntimeException exception) {
                LOGGER.debug("Failed to flush pending configuration during JVM shutdown", exception);
            }
        }, Constants.MOD_ID + "-config-flush");

        try {
            Runtime.getRuntime().addShutdownHook(hook);
            shutdownHookRegistered = true;
        } catch (IllegalStateException exception) {
            LOGGER.debug("Skipping shutdown hook registration because JVM is shutting down");
        } catch (SecurityException exception) {
            LOGGER.warn("Unable to register configuration flush shutdown hook", exception);
        }
    }

    public synchronized void resetForTests() {
        jumpHandler = JumpHandler.noOp();
        toggleFeedback = ToggleFeedback.NO_OP;
        configManager.resetForTests();
        serverStats.clear();
        activeServerId = DEFAULT_SERVER_ID;
        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
        profileSwitchCooldownTicks = 0;

        RuntimeState.reset();
        initialized = false;
    }
}
