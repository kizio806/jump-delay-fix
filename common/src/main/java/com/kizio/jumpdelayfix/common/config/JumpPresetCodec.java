package com.kizio.jumpdelayfix.common.config;

import com.kizio.jumpdelayfix.common.model.JumpProfile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encodes/decodes compact shareable preset strings for runtime settings.
 */
public final class JumpPresetCodec {

    private static final String PRESET_HEADER = "JDF1";

    private JumpPresetCodec() {
    }

    /**
     * Exports profile + runtime config into a URL-safe Base64 preset code.
     */
    public static String exportPreset(JumpProfile profile, JumpRuntimeConfig config) {
        String payload = String.join("|",
                PRESET_HEADER,
                "profile=" + profile.name(),
                "auto=" + boolToFlag(config.autoProfileSwitch()),
                "hud=" + boolToFlag(config.hudEnabled()),
                "hudX=" + config.hudOffsetX(),
                "hudY=" + config.hudOffsetY(),
                "hudScale=" + config.hudScale(),
                "hudProfile=" + boolToFlag(config.hudShowProfileAndPing()),
                "hudTiming=" + boolToFlag(config.hudShowRollbackAndPenalty()),
                "hudMode=" + boolToFlag(config.hudShowModeAndQuality()),
                "hudServer=" + boolToFlag(config.hudShowServer()),
                "hudBar=" + boolToFlag(config.hudShowQualityBar()),
                "shadow=" + boolToFlag(config.shadowMode()),
                "failsafe=" + boolToFlag(config.safetyFailsafe()),
                "minAttempts=" + config.minAttemptsForProfileSwitch(),
                "compRate=" + config.competitiveRollbackRateMax(),
                "stableRate=" + config.stableRollbackRateMin(),
                "failsafeRate=" + config.failsafeRollbackRate()
        );

        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Imports a preset code into a new config snapshot.
     *
     * @param presetCode     encoded preset string
     * @param runtimeConfig  current runtime config used as base
     * @param currentProfile current selected profile used as fallback
     * @return imported preset, or {@code null} when code is invalid
     */
    public static ImportedPreset importPreset(String presetCode, JumpRuntimeConfig runtimeConfig, JumpProfile currentProfile) {
        if (presetCode == null || presetCode.isBlank()) {
            return null;
        }

        String decoded;
        try {
            decoded = new String(Base64.getUrlDecoder().decode(presetCode.trim()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return null;
        }

        String[] parts = decoded.split("\\|");
        if (parts.length == 0 || !PRESET_HEADER.equals(parts[0])) {
            return null;
        }

        JumpRuntimeConfig updatedConfig = runtimeConfig.copy();
        JumpProfile updatedProfile = currentProfile;

        for (int i = 1; i < parts.length; i++) {
            String token = parts[i];
            int separator = token.indexOf('=');
            if (separator <= 0 || separator >= token.length() - 1) {
                continue;
            }

            String key = token.substring(0, separator);
            String value = token.substring(separator + 1);
            updatedProfile = applyToken(updatedConfig, updatedProfile, key, value);
        }

        return new ImportedPreset(updatedConfig, updatedProfile);
    }

    private static JumpProfile applyToken(JumpRuntimeConfig updatedConfig, JumpProfile updatedProfile, String key, String value) {
        return switch (key) {
            case "profile" -> parseProfile(value, updatedProfile);
            case "auto" -> {
                updatedConfig.setAutoProfileSwitch("1".equals(value));
                yield updatedProfile;
            }
            case "hud" -> {
                updatedConfig.setHudEnabled("1".equals(value));
                yield updatedProfile;
            }
            case "hudX" -> {
                parseInt(value, updatedConfig::setHudOffsetX);
                yield updatedProfile;
            }
            case "hudY" -> {
                parseInt(value, updatedConfig::setHudOffsetY);
                yield updatedProfile;
            }
            case "hudScale" -> {
                parseDouble(value, updatedConfig::setHudScale);
                yield updatedProfile;
            }
            case "hudProfile" -> {
                updatedConfig.setHudShowProfileAndPing("1".equals(value));
                yield updatedProfile;
            }
            case "hudTiming" -> {
                updatedConfig.setHudShowRollbackAndPenalty("1".equals(value));
                yield updatedProfile;
            }
            case "hudMode" -> {
                updatedConfig.setHudShowModeAndQuality("1".equals(value));
                yield updatedProfile;
            }
            case "hudServer" -> {
                updatedConfig.setHudShowServer("1".equals(value));
                yield updatedProfile;
            }
            case "hudBar" -> {
                updatedConfig.setHudShowQualityBar("1".equals(value));
                yield updatedProfile;
            }
            case "shadow" -> {
                updatedConfig.setShadowMode("1".equals(value));
                yield updatedProfile;
            }
            case "failsafe" -> {
                updatedConfig.setSafetyFailsafe("1".equals(value));
                yield updatedProfile;
            }
            case "minAttempts" -> {
                parseInt(value, updatedConfig::setMinAttemptsForProfileSwitch);
                yield updatedProfile;
            }
            case "compRate" -> {
                parseDouble(value, updatedConfig::setCompetitiveRollbackRateMax);
                yield updatedProfile;
            }
            case "stableRate" -> {
                parseDouble(value, updatedConfig::setStableRollbackRateMin);
                yield updatedProfile;
            }
            case "failsafeRate" -> {
                parseDouble(value, updatedConfig::setFailsafeRollbackRate);
                yield updatedProfile;
            }
            default -> updatedProfile;
        };
    }

    private static JumpProfile parseProfile(String value, JumpProfile fallback) {
        try {
            return JumpProfile.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static void parseInt(String value, IntConsumer consumer) {
        try {
            consumer.accept(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            // ignore invalid token
        }
    }

    private static void parseDouble(String value, DoubleConsumer consumer) {
        try {
            consumer.accept(Double.parseDouble(value));
        } catch (NumberFormatException ignored) {
            // ignore invalid token
        }
    }

    private static String boolToFlag(boolean value) {
        return value ? "1" : "0";
    }

    /**
     * Decoded preset payload.
     *
     * @param runtimeConfig imported runtime config
     * @param profile       imported profile
     */
    public record ImportedPreset(JumpRuntimeConfig runtimeConfig, JumpProfile profile) {
    }

    @FunctionalInterface
    private interface IntConsumer {
        void accept(int value);
    }

    @FunctionalInterface
    private interface DoubleConsumer {
        void accept(double value);
    }
}
