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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Main runtime facade for the client-side jump delay controller.
 * <p>
 * Loader-specific code only depends on this class and {@link com.kizio.jumpdelayfix.common.api.JumpInput},
 * while all gameplay logic, profile adaptation and persistent configuration are handled here.
 */
public final class JumpDelayFix {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModConstants.MOD_NAME);

    private static final String DEFAULT_SERVER_ID = "global";
    private static final int AUTO_SWITCH_COOLDOWN_TICKS = 40;
    private static final int MAX_TRACKED_SERVERS = 128;
    private static final int CONFIG_SAVE_DEBOUNCE_TICKS = 10;

    private static JumpHandler jumpHandler = JumpHandler.noOp();
    private static ToggleFeedback toggleFeedback = ToggleFeedback.NO_OP;

    private static boolean initialized;
    private static boolean shutdownHookRegistered;

    private static JumpRuntimeConfig runtimeConfig = JumpRuntimeConfig.defaults();
    private static final Map<String, JumpProfile> serverProfileMemory = createServerScopedLruMap();
    private static final Map<String, ServerAdaptiveStats> serverStats = createServerScopedLruMap();

    private static Path configFilePath = Path.of("config", "jumpdelayfix.properties");
    private static String activeServerId = DEFAULT_SERVER_ID;

    private static int lastConfirmedJumps;
    private static int lastRejectedJumps;
    private static int lastShadowPredictions;
    private static int profileSwitchCooldownTicks;
    private static int configSaveCooldownTicks;
    private static boolean configDirty;

    private JumpDelayFix() {
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback) {
        init(input, feedback, Path.of("config"));
    }

    public static synchronized void init(JumpInput input, ToggleFeedback feedback, Path configDirectory) {
        jumpHandler = new JumpHandler(Objects.requireNonNull(input, "input"));
        toggleFeedback = Objects.requireNonNull(feedback, "feedback");

        registerShutdownHookIfNeeded();
        loadConfiguration(Objects.requireNonNull(configDirectory, "configDirectory"));

        ModState.setEnabled(true);
        ModState.setProfile(resolveProfileForServer(activeServerId));
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());

        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
        lastShadowPredictions = 0;
        profileSwitchCooldownTicks = 0;
        configSaveCooldownTicks = 0;
        configDirty = false;
        initialized = true;

        LOGGER.info("{} initialized", ModConstants.MOD_NAME);
    }

    /**
     * Ticks the jump controller once per client tick.
     */
    public static synchronized void onClientTick() {
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

        flushConfigurationIfDue();
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
        markConfigurationDirty();

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, profile.name());
        return profile;
    }

    public static synchronized JumpProfile setProfile(JumpProfile profile) {
        JumpProfile resolvedProfile = Objects.requireNonNull(profile, "profile");

        ModState.setProfile(resolvedProfile);
        rememberProfileForActiveServer(resolvedProfile);

        // Manual profile switch implies user intent. Keep this deterministic until user re-enables auto mode.
        runtimeConfig.setAutoProfileSwitch(false);
        markConfigurationDirty();

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, resolvedProfile.name());
        return resolvedProfile;
    }

    public static synchronized boolean toggleAutoProfileSwitch() {
        runtimeConfig.setAutoProfileSwitch(!runtimeConfig.autoProfileSwitch());
        markConfigurationDirty();
        return runtimeConfig.autoProfileSwitch();
    }

    public static synchronized boolean isAutoProfileSwitchEnabled() {
        return runtimeConfig.autoProfileSwitch();
    }

    public static synchronized boolean toggleHudEnabled() {
        runtimeConfig.setHudEnabled(!runtimeConfig.hudEnabled());
        markConfigurationDirty();
        return runtimeConfig.hudEnabled();
    }

    public static synchronized boolean isHudEnabled() {
        return runtimeConfig.hudEnabled();
    }

    public static synchronized void setHudPosition(int x, int y) {
        int previousX = runtimeConfig.hudOffsetX();
        int previousY = runtimeConfig.hudOffsetY();

        runtimeConfig.setHudOffsetX(x);
        runtimeConfig.setHudOffsetY(y);

        if (runtimeConfig.hudOffsetX() != previousX || runtimeConfig.hudOffsetY() != previousY) {
            markConfigurationDirty();
        }
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
        double previousScale = runtimeConfig.hudScale();
        runtimeConfig.setHudScale(scale);

        if (Double.compare(runtimeConfig.hudScale(), previousScale) != 0) {
            markConfigurationDirty();
        }
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
        markConfigurationDirty();
    }

    public static synchronized boolean toggleHudProfileAndPing() {
        runtimeConfig.setHudShowProfileAndPing(!runtimeConfig.hudShowProfileAndPing());
        markConfigurationDirty();
        return runtimeConfig.hudShowProfileAndPing();
    }

    public static synchronized boolean isHudProfileAndPingVisible() {
        return runtimeConfig.hudShowProfileAndPing();
    }

    public static synchronized boolean toggleHudRollbackAndPenalty() {
        runtimeConfig.setHudShowRollbackAndPenalty(!runtimeConfig.hudShowRollbackAndPenalty());
        markConfigurationDirty();
        return runtimeConfig.hudShowRollbackAndPenalty();
    }

    public static synchronized boolean isHudRollbackAndPenaltyVisible() {
        return runtimeConfig.hudShowRollbackAndPenalty();
    }

    public static synchronized boolean toggleHudModeAndQuality() {
        runtimeConfig.setHudShowModeAndQuality(!runtimeConfig.hudShowModeAndQuality());
        markConfigurationDirty();
        return runtimeConfig.hudShowModeAndQuality();
    }

    public static synchronized boolean isHudModeAndQualityVisible() {
        return runtimeConfig.hudShowModeAndQuality();
    }

    public static synchronized boolean toggleHudServer() {
        runtimeConfig.setHudShowServer(!runtimeConfig.hudShowServer());
        markConfigurationDirty();
        return runtimeConfig.hudShowServer();
    }

    public static synchronized boolean isHudServerVisible() {
        return runtimeConfig.hudShowServer();
    }

    public static synchronized boolean toggleHudQualityBar() {
        runtimeConfig.setHudShowQualityBar(!runtimeConfig.hudShowQualityBar());
        markConfigurationDirty();
        return runtimeConfig.hudShowQualityBar();
    }

    public static synchronized boolean isHudQualityBarVisible() {
        return runtimeConfig.hudShowQualityBar();
    }

    public static synchronized boolean toggleShadowMode() {
        runtimeConfig.setShadowMode(!runtimeConfig.shadowMode());
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        markConfigurationDirty();
        return runtimeConfig.shadowMode();
    }

    public static synchronized boolean isShadowModeEnabled() {
        return runtimeConfig.shadowMode();
    }

    public static synchronized boolean toggleSafetyFailsafe() {
        runtimeConfig.setSafetyFailsafe(!runtimeConfig.safetyFailsafe());
        markConfigurationDirty();
        return runtimeConfig.safetyFailsafe();
    }

    public static synchronized boolean isSafetyFailsafeEnabled() {
        return runtimeConfig.safetyFailsafe();
    }

    public static synchronized int setMinAttemptsForProfileSwitch(int minAttemptsForProfileSwitch) {
        int previous = runtimeConfig.minAttemptsForProfileSwitch();
        runtimeConfig.setMinAttemptsForProfileSwitch(minAttemptsForProfileSwitch);

        if (runtimeConfig.minAttemptsForProfileSwitch() != previous) {
            markConfigurationDirty();
        }
        return runtimeConfig.minAttemptsForProfileSwitch();
    }

    public static synchronized int adjustMinAttemptsForProfileSwitch(int delta) {
        return setMinAttemptsForProfileSwitch(runtimeConfig.minAttemptsForProfileSwitch() + delta);
    }

    public static synchronized double setCompetitiveRollbackRateMax(double rollbackRateMax) {
        double previousCompetitive = runtimeConfig.competitiveRollbackRateMax();
        double previousStable = runtimeConfig.stableRollbackRateMin();
        double previousFailsafe = runtimeConfig.failsafeRollbackRate();

        runtimeConfig.setCompetitiveRollbackRateMax(rollbackRateMax);
        normalizeRuntimeThresholds();

        if (Double.compare(runtimeConfig.competitiveRollbackRateMax(), previousCompetitive) != 0
                || Double.compare(runtimeConfig.stableRollbackRateMin(), previousStable) != 0
                || Double.compare(runtimeConfig.failsafeRollbackRate(), previousFailsafe) != 0) {
            markConfigurationDirty();
        }
        return runtimeConfig.competitiveRollbackRateMax();
    }

    public static synchronized double adjustCompetitiveRollbackRateMax(double delta) {
        return setCompetitiveRollbackRateMax(runtimeConfig.competitiveRollbackRateMax() + delta);
    }

    public static synchronized double setStableRollbackRateMin(double rollbackRateMin) {
        double previousStable = runtimeConfig.stableRollbackRateMin();
        double previousFailsafe = runtimeConfig.failsafeRollbackRate();

        runtimeConfig.setStableRollbackRateMin(rollbackRateMin);
        normalizeRuntimeThresholds();

        if (Double.compare(runtimeConfig.stableRollbackRateMin(), previousStable) != 0
                || Double.compare(runtimeConfig.failsafeRollbackRate(), previousFailsafe) != 0) {
            markConfigurationDirty();
        }
        return runtimeConfig.stableRollbackRateMin();
    }

    public static synchronized double adjustStableRollbackRateMin(double delta) {
        return setStableRollbackRateMin(runtimeConfig.stableRollbackRateMin() + delta);
    }

    public static synchronized double setFailsafeRollbackRate(double rollbackRate) {
        double previous = runtimeConfig.failsafeRollbackRate();
        runtimeConfig.setFailsafeRollbackRate(rollbackRate);
        normalizeRuntimeThresholds();

        if (Double.compare(runtimeConfig.failsafeRollbackRate(), previous) != 0) {
            markConfigurationDirty();
        }
        return runtimeConfig.failsafeRollbackRate();
    }

    public static synchronized double adjustFailsafeRollbackRate(double delta) {
        return setFailsafeRollbackRate(runtimeConfig.failsafeRollbackRate() + delta);
    }

    public static synchronized void resetSettingsToDefaults() {
        runtimeConfig = JumpRuntimeConfig.defaults();
        normalizeRuntimeThresholds();
        jumpHandler.setShadowMode(runtimeConfig.shadowMode());
        markConfigurationDirty();
    }

    public static synchronized void clearServerProfileMemory() {
        serverProfileMemory.clear();
        serverStats.clear();
        ModState.setProfile(JumpProfile.SMART);
        markConfigurationDirty();
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
        markConfigurationDirty();
        return true;
    }

    /**
     * Forces immediate persistence of pending config changes.
     * <p>
     * Intended for screen close/shutdown hooks where we do not want to wait for tick debounce.
     */
    public static synchronized void flushPendingConfiguration() {
        flushConfigurationNow();
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
        markConfigurationDirty();
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
        return rawServerId.trim().toLowerCase(Locale.ROOT);
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
        configSaveCooldownTicks = 0;
        configDirty = false;
    }

    private static void registerShutdownHookIfNeeded() {
        if (shutdownHookRegistered) {
            return;
        }

        Thread hook = new Thread(() -> {
            try {
                flushPendingConfiguration();
            } catch (RuntimeException exception) {
                LOGGER.debug("Failed to flush pending configuration during JVM shutdown", exception);
            }
        }, ModConstants.MOD_ID + "-config-flush");

        try {
            Runtime.getRuntime().addShutdownHook(hook);
            shutdownHookRegistered = true;
        } catch (IllegalStateException exception) {
            LOGGER.debug("Skipping shutdown hook registration because JVM is shutting down");
        } catch (SecurityException exception) {
            LOGGER.warn("Unable to register configuration flush shutdown hook", exception);
        }
    }

    private static void persistConfigurationNow() {
        JumpConfigStorage.save(configFilePath, runtimeConfig, serverProfileMemory);
    }

    private static void markConfigurationDirty() {
        configDirty = true;
        configSaveCooldownTicks = CONFIG_SAVE_DEBOUNCE_TICKS;
    }

    private static void flushConfigurationIfDue() {
        if (!configDirty) {
            return;
        }

        if (configSaveCooldownTicks > 0) {
            configSaveCooldownTicks--;
            return;
        }

        persistConfigurationNow();
        configDirty = false;
    }

    private static void flushConfigurationNow() {
        if (!configDirty) {
            return;
        }

        persistConfigurationNow();
        configDirty = false;
        configSaveCooldownTicks = 0;
    }

    private static <V> Map<String, V> createServerScopedLruMap() {
        return new LinkedHashMap<>(16, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                return size() > MAX_TRACKED_SERVERS;
            }
        };
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
        configSaveCooldownTicks = 0;
        configDirty = false;

        ModState.setEnabled(true);
        ModState.setProfile(JumpProfile.SMART);
        initialized = false;
    }
}
