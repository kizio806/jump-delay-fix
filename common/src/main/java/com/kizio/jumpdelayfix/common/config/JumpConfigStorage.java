package com.kizio.jumpdelayfix.common.config;

import com.kizio.jumpdelayfix.common.model.JumpProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class JumpConfigStorage {

    private static final String KEY_AUTO_PROFILE_SWITCH = "autoProfileSwitch";
    private static final String KEY_HUD_ENABLED = "hudEnabled";
    private static final String KEY_SHADOW_MODE = "shadowMode";
    private static final String KEY_SAFETY_FAILSAFE = "safetyFailsafe";
    private static final String KEY_HUD_OFFSET_X = "hudOffsetX";
    private static final String KEY_HUD_OFFSET_Y = "hudOffsetY";
    private static final String KEY_HUD_SCALE = "hudScale";
    private static final String KEY_HUD_SHOW_PROFILE = "hudShowProfileAndPing";
    private static final String KEY_HUD_SHOW_TIMING = "hudShowRollbackAndPenalty";
    private static final String KEY_HUD_SHOW_MODE = "hudShowModeAndQuality";
    private static final String KEY_HUD_SHOW_SERVER = "hudShowServer";
    private static final String KEY_HUD_SHOW_BAR = "hudShowQualityBar";
    private static final String KEY_MIN_ATTEMPTS = "minAttemptsForProfileSwitch";
    private static final String KEY_COMPETITIVE_RATE = "competitiveRollbackRateMax";
    private static final String KEY_STABLE_RATE = "stableRollbackRateMin";
    private static final String KEY_FAILSAFE_RATE = "failsafeRollbackRate";

    private static final String KEY_SERVER_PROFILE_PREFIX = "serverProfile.";

    private JumpConfigStorage() {
    }

    public static LoadedConfig load(Path filePath) {
        Objects.requireNonNull(filePath, "filePath");

        JumpRuntimeConfig config = JumpRuntimeConfig.defaults();
        Map<String, JumpProfile> profiles = new HashMap<>();

        if (!Files.exists(filePath)) {
            return new LoadedConfig(config, profiles);
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ignored) {
            return new LoadedConfig(config, profiles);
        }

        config.setAutoProfileSwitch(getBoolean(properties, KEY_AUTO_PROFILE_SWITCH, config.autoProfileSwitch()));
        config.setHudEnabled(getBoolean(properties, KEY_HUD_ENABLED, config.hudEnabled()));
        config.setShadowMode(getBoolean(properties, KEY_SHADOW_MODE, config.shadowMode()));
        config.setSafetyFailsafe(getBoolean(properties, KEY_SAFETY_FAILSAFE, config.safetyFailsafe()));
        config.setHudOffsetX(getInt(properties, KEY_HUD_OFFSET_X, config.hudOffsetX()));
        config.setHudOffsetY(getInt(properties, KEY_HUD_OFFSET_Y, config.hudOffsetY()));
        config.setHudScale(getDouble(properties, KEY_HUD_SCALE, config.hudScale()));
        config.setHudShowProfileAndPing(getBoolean(properties, KEY_HUD_SHOW_PROFILE, config.hudShowProfileAndPing()));
        config.setHudShowRollbackAndPenalty(getBoolean(properties, KEY_HUD_SHOW_TIMING, config.hudShowRollbackAndPenalty()));
        config.setHudShowModeAndQuality(getBoolean(properties, KEY_HUD_SHOW_MODE, config.hudShowModeAndQuality()));
        config.setHudShowServer(getBoolean(properties, KEY_HUD_SHOW_SERVER, config.hudShowServer()));
        config.setHudShowQualityBar(getBoolean(properties, KEY_HUD_SHOW_BAR, config.hudShowQualityBar()));
        config.setMinAttemptsForProfileSwitch(getInt(properties, KEY_MIN_ATTEMPTS, config.minAttemptsForProfileSwitch()));
        config.setCompetitiveRollbackRateMax(getDouble(properties, KEY_COMPETITIVE_RATE, config.competitiveRollbackRateMax()));
        config.setStableRollbackRateMin(getDouble(properties, KEY_STABLE_RATE, config.stableRollbackRateMin()));
        config.setFailsafeRollbackRate(getDouble(properties, KEY_FAILSAFE_RATE, config.failsafeRollbackRate()));

        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(KEY_SERVER_PROFILE_PREFIX)) {
                continue;
            }

            String encodedServer = key.substring(KEY_SERVER_PROFILE_PREFIX.length());
            String serverId = decodeServerId(encodedServer);
            if (serverId == null || serverId.isBlank()) {
                continue;
            }

            JumpProfile profile = parseProfile(properties.getProperty(key));
            if (profile != null) {
                profiles.put(serverId, profile);
            }
        }

        return new LoadedConfig(config, profiles);
    }

    public static void save(Path filePath, JumpRuntimeConfig config, Map<String, JumpProfile> serverProfiles) {
        Objects.requireNonNull(filePath, "filePath");
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(serverProfiles, "serverProfiles");

        Properties properties = new Properties();

        properties.setProperty(KEY_AUTO_PROFILE_SWITCH, Boolean.toString(config.autoProfileSwitch()));
        properties.setProperty(KEY_HUD_ENABLED, Boolean.toString(config.hudEnabled()));
        properties.setProperty(KEY_SHADOW_MODE, Boolean.toString(config.shadowMode()));
        properties.setProperty(KEY_SAFETY_FAILSAFE, Boolean.toString(config.safetyFailsafe()));
        properties.setProperty(KEY_HUD_OFFSET_X, Integer.toString(config.hudOffsetX()));
        properties.setProperty(KEY_HUD_OFFSET_Y, Integer.toString(config.hudOffsetY()));
        properties.setProperty(KEY_HUD_SCALE, Double.toString(config.hudScale()));
        properties.setProperty(KEY_HUD_SHOW_PROFILE, Boolean.toString(config.hudShowProfileAndPing()));
        properties.setProperty(KEY_HUD_SHOW_TIMING, Boolean.toString(config.hudShowRollbackAndPenalty()));
        properties.setProperty(KEY_HUD_SHOW_MODE, Boolean.toString(config.hudShowModeAndQuality()));
        properties.setProperty(KEY_HUD_SHOW_SERVER, Boolean.toString(config.hudShowServer()));
        properties.setProperty(KEY_HUD_SHOW_BAR, Boolean.toString(config.hudShowQualityBar()));
        properties.setProperty(KEY_MIN_ATTEMPTS, Integer.toString(config.minAttemptsForProfileSwitch()));
        properties.setProperty(KEY_COMPETITIVE_RATE, Double.toString(config.competitiveRollbackRateMax()));
        properties.setProperty(KEY_STABLE_RATE, Double.toString(config.stableRollbackRateMin()));
        properties.setProperty(KEY_FAILSAFE_RATE, Double.toString(config.failsafeRollbackRate()));

        serverProfiles.forEach((serverId, profile) -> {
            if (serverId != null && !serverId.isBlank() && profile != null) {
                properties.setProperty(KEY_SERVER_PROFILE_PREFIX + encodeServerId(serverId), profile.name());
            }
        });

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (OutputStream output = Files.newOutputStream(filePath)) {
                properties.store(output, "JumpDelayFix configuration");
            }
        } catch (IOException ignored) {
            // Keep runtime state even if disk write fails.
        }
    }

    private static boolean getBoolean(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private static int getInt(Properties properties, String key, int fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double getDouble(Properties properties, String key, double fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static JumpProfile parseProfile(String value) {
        if (value == null) {
            return null;
        }

        try {
            return JumpProfile.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static String encodeServerId(String serverId) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(serverId.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeServerId(String encodedServerId) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedServerId);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public record LoadedConfig(JumpRuntimeConfig config, Map<String, JumpProfile> serverProfiles) {
    }
}
