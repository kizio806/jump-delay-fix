package com.kizio.jumpdelayfix.common;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import com.kizio.jumpdelayfix.common.api.ToggleFeedback;
import com.kizio.jumpdelayfix.common.config.JumpConfigStorage;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.feature.JumpHandler;
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
 */
public final class JumpDelayFix {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModConstants.MOD_NAME);

    private static final String DEFAULT_SERVER_ID = "global";
    private static final int AUTO_SWITCH_COOLDOWN_TICKS = 40;
    private static final int AUTO_SWITCH_MIN_ATTEMPTS = 8;
    private static final int STABLE_PROFILE_MIN_LATENCY_MS = 210;
    private static final int COMPETITIVE_PROFILE_MAX_LATENCY_MS = 95;
    private static final double COMPETITIVE_PROFILE_MAX_ROLLBACK_RATE = 0.08D;
    private static final double STABLE_PROFILE_MIN_ROLLBACK_RATE = 0.30D;
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

        lastConfirmedJumps = 0;
        lastRejectedJumps = 0;
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
        rememberManualProfileSelection(profile);

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, profile.name());
        return profile;
    }

    public static synchronized JumpProfile setProfile(JumpProfile profile) {
        JumpProfile resolvedProfile = Objects.requireNonNull(profile, "profile");
        ModState.setProfile(resolvedProfile);
        rememberManualProfileSelection(resolvedProfile);

        LOGGER.debug("{} profile {}", ModConstants.MOD_NAME, resolvedProfile.name());
        return resolvedProfile;
    }

    public static synchronized boolean toggleAutoProfileSwitch() {
        boolean enabled = !runtimeConfig.autoProfileSwitch();
        runtimeConfig.setAutoProfileSwitch(enabled);
        profileSwitchCooldownTicks = 0;
        markConfigurationDirty();
        return enabled;
    }

    public static synchronized boolean isAutoProfileSwitchEnabled() {
        return runtimeConfig.autoProfileSwitch();
    }

    /**
     * Forces immediate persistence of pending config changes.
     * <p>
     * Intended for screen close/shutdown hooks where we do not want to wait for tick debounce.
     */
    public static synchronized void flushPendingConfiguration() {
        flushConfigurationNow();
    }

    private static void rememberManualProfileSelection(JumpProfile profile) {
        rememberProfileForActiveServer(profile);

        if (runtimeConfig.autoProfileSwitch()) {
            runtimeConfig.setAutoProfileSwitch(false);
        }

        profileSwitchCooldownTicks = 0;
        markConfigurationDirty();
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
    }

    private static void updateServerStats() {
        int confirmed = jumpHandler.getConfirmedJumpCount();
        int rejected = jumpHandler.getRejectedJumpCount();

        int confirmedDelta = Math.max(0, confirmed - lastConfirmedJumps);
        int rejectedDelta = Math.max(0, rejected - lastRejectedJumps);

        lastConfirmedJumps = confirmed;
        lastRejectedJumps = rejected;

        ServerAdaptiveStats stats = serverStats.computeIfAbsent(activeServerId, ignored -> new ServerAdaptiveStats());
        stats.update(confirmedDelta, rejectedDelta);
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
        if (attempts < AUTO_SWITCH_MIN_ATTEMPTS) {
            return;
        }

        JumpProfile targetProfile = chooseAutoProfile(jumpHandler.getLatencyMs(), stats.rollbackRate());
        JumpProfile currentProfile = ModState.getProfile();
        if (targetProfile == currentProfile) {
            return;
        }

        ModState.setProfile(targetProfile);
        rememberProfileForActiveServer(targetProfile);
        markConfigurationDirty();
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
        profileSwitchCooldownTicks = 0;
        configSaveCooldownTicks = 0;
        configDirty = false;

        ModState.setEnabled(true);
        ModState.setProfile(JumpProfile.SMART);
        initialized = false;
    }
}
