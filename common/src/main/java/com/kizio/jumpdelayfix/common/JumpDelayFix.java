package com.kizio.jumpdelayfix.common;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.api.ToggleFeedback;
import com.kizio.jumpdelayfix.common.config.JumpConfigStorage;
import com.kizio.jumpdelayfix.common.config.JumpPresetCodec;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.feature.JumpHandler;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import com.kizio.jumpdelayfix.common.model.JumpProfile;
import com.kizio.jumpdelayfix.common.model.ServerAdaptiveStats;
import com.kizio.jumpdelayfix.common.state.ModState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class JumpDelayFix {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModConstants.MOD_NAME);

    private static final String DEFAULT_SERVER_ID = "global";
    private static final int AUTO_SWITCH_COOLDOWN_TICKS = 40;

    private static JumpHandler jumpHandler = JumpHandler.noOp();
    private static ToggleFeedback toggleFeedback = ToggleFeedback.NO_OP;

    private static boolean initialized;

    private static JumpRuntimeConfig runtimeConfig = JumpRuntimeConfig.defaults();
    private static final Map<String, JumpProfile> serverProfileMemory = new HashMap<>();
    private static final Map<String, ServerAdaptiveStats> serverStats = new HashMap<>();

    private static Path configFilePath = Path.of("config", "jumpdelayfix.properties");
    private static String activeServerId = DEFAULT_SERVER_ID;

    private static int lastConfirmedJumps;
    private static int lastRejectedJumps;
    private static int lastShadowPredictions;
    private static int profileSwitchCooldownTicks;

    private JumpDelayFix() {
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback) {
        init(input, feedback, Path.of("config"));
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback, Path configDirectory) {
        jumpHandler = new JumpHandler(Objects.requireNonNull(input, "input"));
        toggleFeedback = Objects.requireNonNull(feedback, "feedback");

        loadConfiguration(Objects.requireNonNull(configDirectory, "configDirectory"));

        ModState.setEnabled(true);
        ModState.setProfile(resolveProfileForServer(activeServerId));
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());

        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
        lastShadowPredictions = 0;
        profileSwitchCooldownTicks = 0;
        initialized = true;

        LOGGER.info("{} initialized", ModConstants.MOD_NAME);
    }

    public static void onClientTick() {
        if (!initialized) {
            return;
        }

        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        jumpHandler.tick();

        synchronizeServerContext();
        updateServerStats();

        if (runtimeConfig.autoProfileSwitch()) {
            applyAutoProfileSwitch();
        }

        if (profileSwitchCooldownTicks > 0) {
            profileSwitchCooldownTicks--;
        }
    }

    public static synchronized boolean toggleEnabled() {
        boolean enabled = ModState.toggle();
        toggleFeedback.onToggle(enabled);

        LOGGER.debug("{} toggled {}", ModConstants.MOD_NAME, enabled ? "on" : "off");
        return enabled;
    }

    public static boolean isEnabled() {
        return ModState.isEnabled();
    }

    public static JumpProfile getProfile() {
        return ModState.getProfile();
    }

    public static synchronized JumpProfile cycleProfile() {
        JumpProfile profile = ModState.cycleProfile();
        rememberProfileForActiveServer(profile);

        // Manual profile switch implies user intent. Keep this deterministic until user re-enables auto mode.
        runtimeConfig.setAutoProfileSwitch(false);
        saveConfiguration();

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, profile.name());
        return profile;
    }

    public static synchronized JumpProfile setProfile(JumpProfile profile) {
        JumpProfile resolvedProfile = Objects.requireNonNull(profile, "profile");

        ModState.setProfile(resolvedProfile);
        rememberProfileForActiveServer(resolvedProfile);

        // Manual profile switch implies user intent. Keep this deterministic until user re-enables auto mode.
        runtimeConfig.setAutoProfileSwitch(false);
        saveConfiguration();

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, resolvedProfile.name());
        return resolvedProfile;
    }

    public static synchronized boolean toggleAutoProfileSwitch() {
        runtimeConfig.setAutoProfileSwitch(!runtimeConfig.autoProfileSwitch());
        saveConfiguration();
        return runtimeConfig.autoProfileSwitch();
    }

    public static synchronized boolean isAutoProfileSwitchEnabled() {
        return runtimeConfig.autoProfileSwitch();
    }

    public static synchronized boolean toggleHudEnabled() {
        runtimeConfig.setHudEnabled(!runtimeConfig.hudEnabled());
        saveConfiguration();
        return runtimeConfig.hudEnabled();
    }

    public static synchronized boolean isHudEnabled() {
        return runtimeConfig.hudEnabled();
    }

    public static synchronized void setHudPosition(int x, int y) {
        runtimeConfig.setHudOffsetX(x);
        runtimeConfig.setHudOffsetY(y);
        saveConfiguration();
    }

    public static synchronized void moveHudBy(int dx, int dy) {
        setHudPosition(runtimeConfig.hudOffsetX() + dx, runtimeConfig.hudOffsetY() + dy);
    }

    public static synchronized int getHudOffsetX() {
        return runtimeConfig.hudOffsetX();
    }

    public static synchronized int getHudOffsetY() {
        return runtimeConfig.hudOffsetY();
    }

    public static synchronized double setHudScale(double scale) {
        runtimeConfig.setHudScale(scale);
        saveConfiguration();
        return runtimeConfig.hudScale();
    }

    public static synchronized double adjustHudScale(double delta) {
        return setHudScale(runtimeConfig.hudScale() + delta);
    }

    public static synchronized double getHudScale() {
        return runtimeConfig.hudScale();
    }

    public static synchronized void resetHudLayout() {
        JumpRuntimeConfig defaults = JumpRuntimeConfig.defaults();
        runtimeConfig.setHudOffsetX(defaults.hudOffsetX());
        runtimeConfig.setHudOffsetY(defaults.hudOffsetY());
        runtimeConfig.setHudScale(defaults.hudScale());
        saveConfiguration();
    }

    public static synchronized boolean toggleHudProfileAndPing() {
        runtimeConfig.setHudShowProfileAndPing(!runtimeConfig.hudShowProfileAndPing());
        saveConfiguration();
        return runtimeConfig.hudShowProfileAndPing();
    }

    public static synchronized boolean isHudProfileAndPingVisible() {
        return runtimeConfig.hudShowProfileAndPing();
    }

    public static synchronized boolean toggleHudRollbackAndPenalty() {
        runtimeConfig.setHudShowRollbackAndPenalty(!runtimeConfig.hudShowRollbackAndPenalty());
        saveConfiguration();
        return runtimeConfig.hudShowRollbackAndPenalty();
    }

    public static synchronized boolean isHudRollbackAndPenaltyVisible() {
        return runtimeConfig.hudShowRollbackAndPenalty();
    }

    public static synchronized boolean toggleHudModeAndQuality() {
        runtimeConfig.setHudShowModeAndQuality(!runtimeConfig.hudShowModeAndQuality());
        saveConfiguration();
        return runtimeConfig.hudShowModeAndQuality();
    }

    public static synchronized boolean isHudModeAndQualityVisible() {
        return runtimeConfig.hudShowModeAndQuality();
    }

    public static synchronized boolean toggleHudServer() {
        runtimeConfig.setHudShowServer(!runtimeConfig.hudShowServer());
        saveConfiguration();
        return runtimeConfig.hudShowServer();
    }

    public static synchronized boolean isHudServerVisible() {
        return runtimeConfig.hudShowServer();
    }

    public static synchronized boolean toggleHudQualityBar() {
        runtimeConfig.setHudShowQualityBar(!runtimeConfig.hudShowQualityBar());
        saveConfiguration();
        return runtimeConfig.hudShowQualityBar();
    }

    public static synchronized boolean isHudQualityBarVisible() {
        return runtimeConfig.hudShowQualityBar();
    }

    public static synchronized boolean toggleShadowMode() {
        runtimeConfig.setShadowMode(!runtimeConfig.shadowMode());
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        saveConfiguration();
        return runtimeConfig.shadowMode();
    }

    public static synchronized boolean isShadowModeEnabled() {
        return runtimeConfig.shadowMode();
    }

    public static synchronized boolean toggleSafetyFailsafe() {
        runtimeConfig.setSafetyFailsafe(!runtimeConfig.safetyFailsafe());
        saveConfiguration();
        return runtimeConfig.safetyFailsafe();
    }

    public static synchronized boolean isSafetyFailsafeEnabled() {
        return runtimeConfig.safetyFailsafe();
    }

    public static synchronized int setMinAttemptsForProfileSwitch(int minAttemptsForProfileSwitch) {
        runtimeConfig.setMinAttemptsForProfileSwitch(minAttemptsForProfileSwitch);
        saveConfiguration();
        return runtimeConfig.minAttemptsForProfileSwitch();
    }

    public static synchronized double setCompetitiveRollbackRateMax(double rollbackRateMax) {
        runtimeConfig.setCompetitiveRollbackRateMax(rollbackRateMax);
        normalizeRuntimeThresholds();
        saveConfiguration();
        return runtimeConfig.competitiveRollbackRateMax();
    }

    public static synchronized double setStableRollbackRateMin(double rollbackRateMin) {
        runtimeConfig.setStableRollbackRateMin(rollbackRateMin);
        normalizeRuntimeThresholds();
        saveConfiguration();
        return runtimeConfig.stableRollbackRateMin();
    }

    public static synchronized double setFailsafeRollbackRate(double rollbackRate) {
        runtimeConfig.setFailsafeRollbackRate(rollbackRate);
        normalizeRuntimeThresholds();
        saveConfiguration();
        return runtimeConfig.failsafeRollbackRate();
    }

    public static synchronized void resetSettingsToDefaults() {
        runtimeConfig = JumpRuntimeConfig.defaults();
        normalizeRuntimeThresholds();
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        saveConfiguration();
    }

    public static synchronized void clearServerProfileMemory() {
        serverProfileMemory.clear();
        serverStats.clear();
        ModState.setProfile(JumpProfile.SMART);
        saveConfiguration();
    }

    public static synchronized JumpRuntimeConfig getRuntimeConfig() {
        return runtimeConfig.copy();
    }

    public static synchronized JumpDiagnostics getDiagnostics() {
        if (!initialized) {
            return JumpDiagnostics.empty();
        }

        ServerAdaptiveStats stats = serverStats.get(activeServerId);
        int confirmed = stats == null ? 0 : stats.confirmedJumps();
        int rejected = stats == null ? 0 : stats.rejectedJumps();
        int shadowPredictions = stats == null ? 0 : stats.shadowPredictions();
        double rollbackRate = stats == null ? 0.0D : stats.rollbackRate();

        return new JumpDiagnostics(
                activeServerId,
                ModState.getProfile(),
                ModState.isEnabled(),
                runtimeConfig.autoProfileSwitch(),
                runtimeConfig.shadowMode(),
                runtimeConfig.hudEnabled(),
                runtimeConfig.safetyFailsafe(),
                jumpHandler.getLatencyMs(),
                jumpHandler.getAdaptivePenaltyTicks(),
                jumpHandler.getRequiredGroundedTicks(),
                confirmed,
                rejected,
                shadowPredictions,
                rollbackRate
        );
    }

    public static synchronized String exportCurrentPresetCode() {
        return JumpPresetCodec.exportPreset(ModState.getProfile(), runtimeConfig);
    }

    public static synchronized boolean importPresetCode(String presetCode) {
        JumpPresetCodec.ImportedPreset importedPreset = JumpPresetCodec.importPreset(
                presetCode,
                runtimeConfig,
                ModState.getProfile()
        );
        if (importedPreset == null) {
            return false;
        }

        runtimeConfig = importedPreset.runtimeConfig();
        normalizeRuntimeThresholds();
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        ModState.setProfile(importedPreset.profile());
        rememberProfileForActiveServer(importedPreset.profile());
        saveConfiguration();
        return true;
    }

    private static void synchronizeServerContext() {
        String serverId = normalizeServerId(jumpHandler.getServerIdentifier());

        if (Objects.equals(serverId, activeServerId)) {
            return;
        }

        activeServerId = serverId;
        ModState.setProfile(resolveProfileForServer(activeServerId));

        // Avoid cross-server stat deltas after switching sessions.
        lastConfirmedJumps = jumpHandler.getConfirmedJumpCount();
        lastRejectedJumps = jumpHandler.getRejectedJumpCount();
        lastShadowPredictions = jumpHandler.getShadowJumpPredictionCount();
    }

    private static void updateServerStats() {
        int confirmed = jumpHandler.getConfirmedJumpCount();
        int rejected = jumpHandler.getRejectedJumpCount();
        int shadowPredictions = jumpHandler.getShadowJumpPredictionCount();

        int confirmedDelta = Math.max(0, confirmed - lastConfirmedJumps);
        int rejectedDelta = Math.max(0, rejected - lastRejectedJumps);
        int shadowDelta = Math.max(0, shadowPredictions - lastShadowPredictions);

        lastConfirmedJumps = confirmed;
        lastRejectedJumps = rejected;
        lastShadowPredictions = shadowPredictions;

        ServerAdaptiveStats stats = serverStats.computeIfAbsent(activeServerId, ignored -> new ServerAdaptiveStats());
        stats.update(confirmedDelta, rejectedDelta, shadowDelta);
    }

    private static void applyAutoProfileSwitch() {
        if (profileSwitchCooldownTicks > 0) {
            return;
        }

        ServerAdaptiveStats stats = serverStats.get(activeServerId);
        if (stats == null) {
            return;
        }

        int attempts = stats.confirmedJumps() + stats.rejectedJumps();
        if (attempts < runtimeConfig.minAttemptsForProfileSwitch()) {
            return;
        }

        int latencyMs = jumpHandler.getLatencyMs();
        double rollbackRate = stats.rollbackRate();

        JumpProfile targetProfile = chooseAutoProfile(latencyMs, rollbackRate);
        if (runtimeConfig.safetyFailsafe() && rollbackRate >= runtimeConfig.failsafeRollbackRate()) {
            targetProfile = JumpProfile.STABLE;
        }

        JumpProfile currentProfile = ModState.getProfile();
        if (targetProfile == currentProfile) {
            return;
        }

        ModState.setProfile(targetProfile);
        rememberProfileForActiveServer(targetProfile);
        saveConfiguration();
        profileSwitchCooldownTicks = AUTO_SWITCH_COOLDOWN_TICKS;

        LOGGER.debug("Auto-switched profile to {} (server={}, ping={}ms, rollbackRate={})",
                targetProfile.name(),
                activeServerId,
                latencyMs,
                rollbackRate
        );
    }

    private static JumpProfile chooseAutoProfile(int latencyMs, double rollbackRate) {
        if (rollbackRate >= runtimeConfig.stableRollbackRateMin() || latencyMs >= 210) {
            return JumpProfile.STABLE;
        }

        if (rollbackRate <= runtimeConfig.competitiveRollbackRateMax() && latencyMs >= 0 && latencyMs <= 95) {
            return JumpProfile.COMPETITIVE;
        }

        return JumpProfile.SMART;
    }

    private static String normalizeServerId(String rawServerId) {
        if (rawServerId == null || rawServerId.isBlank()) {
            return DEFAULT_SERVER_ID;
        }
        return rawServerId.trim().toLowerCase();
    }

    private static JumpProfile resolveProfileForServer(String serverId) {
        return serverProfileMemory.getOrDefault(serverId, JumpProfile.SMART);
    }

    private static void rememberProfileForActiveServer(JumpProfile profile) {
        serverProfileMemory.put(activeServerId, profile);
    }

    private static void loadConfiguration(Path configDirectory) {
        configFilePath = configDirectory.resolve("jumpdelayfix.properties");

        JumpConfigStorage.LoadedConfig loadedConfig = JumpConfigStorage.load(configFilePath);

        runtimeConfig = loadedConfig.config().copy();
        normalizeRuntimeThresholds();
        serverProfileMemory.clear();
        serverProfileMemory.putAll(loadedConfig.serverProfiles());

        serverStats.clear();
        activeServerId = DEFAULT_SERVER_ID;
    }

    private static void saveConfiguration() {
        JumpConfigStorage.save(configFilePath, runtimeConfig, serverProfileMemory);
    }

    private static void normalizeRuntimeThresholds() {
        double competitiveRate = runtimeConfig.competitiveRollbackRateMax();
        double stableRate = runtimeConfig.stableRollbackRateMin();
        double failsafeRate = runtimeConfig.failsafeRollbackRate();

        if (stableRate < competitiveRate) {
            stableRate = competitiveRate;
        }

        if (failsafeRate < stableRate) {
            failsafeRate = stableRate;
        }

        runtimeConfig.setStableRollbackRateMin(stableRate);
        runtimeConfig.setFailsafeRollbackRate(failsafeRate);
    }

    static synchronized void resetForTests() {
        jumpHandler = JumpHandler.noOp();
        toggleFeedback = ToggleFeedback.NO_OP;
        runtimeConfig = JumpRuntimeConfig.defaults();
        serverProfileMemory.clear();
        serverStats.clear();
        configFilePath = Path.of("config", "jumpdelayfix.properties");
        activeServerId = DEFAULT_SERVER_ID;
        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
        lastShadowPredictions = 0;
        profileSwitchCooldownTicks = 0;

        ModState.setEnabled(true);
        ModState.setProfile(JumpProfile.SMART);
        initialized = false;
    }
}
