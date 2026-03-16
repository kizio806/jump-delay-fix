package com.kizio.jumpdelayfix.common.client.hud;

import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Builds HUD presentation data from runtime diagnostics and user configuration.
 */
public final class HudPresentationService {

    public static final int PANEL_WIDTH = 250;
    public static final int TEXT_X = 6;
    public static final int LINE_HEIGHT = 11;
    public static final String HUD_TITLE = "JumpDelayFix";

    private static final int PANEL_BASE_HEIGHT = 26;
    private static final int QUALITY_BAR_HEIGHT = 11;
    private static final int SERVER_ID_LIMIT = 28;

    private static final int QUALITY_HIGH_RISK_COLOR = 0xFF6A6A;
    private static final int QUALITY_MODERATE_COLOR = 0xFFCA6D;
    private static final int QUALITY_STABLE_COLOR = 0x7CFFB4;

    private HudPresentationService() {
    }

    /**
     * Creates a loader-independent HUD model.
     *
     * @param diagnostics current runtime diagnostics
     * @param config      current runtime HUD configuration
     * @return immutable HUD render model
     */
    public static HudPresentationModel build(JumpDiagnostics diagnostics, JumpRuntimeConfig config) {
        Objects.requireNonNull(diagnostics, "diagnostics");
        Objects.requireNonNull(config, "config");

        int rollbackPercent = rollbackPercent(diagnostics.rollbackRate());
        int qualityColor = qualityColor(diagnostics.rollbackRate());

        List<HudLine> lines = new ArrayList<>(4);
        if (config.hudShowProfileAndPing()) {
            lines.add(new HudLine(
                    String.format(
                            Locale.ROOT,
                            "Profile: %s  Ping: %dms",
                            diagnostics.profile().name(),
                            diagnostics.latencyMs()
                    ),
                    0xFFFFFF
            ));
        }
        if (config.hudShowRollbackAndPenalty()) {
            lines.add(new HudLine(
                    String.format(
                            Locale.ROOT,
                            "Rollback: %d%%  Penalty: %d  ReqTicks: %d",
                            rollbackPercent,
                            diagnostics.adaptivePenaltyTicks(),
                            diagnostics.requiredGroundedTicks()
                    ),
                    0xFFFFFF
            ));
        }
        if (config.hudShowModeAndQuality()) {
            lines.add(new HudLine(
                    "Mode: " + (diagnostics.shadowMode() ? "Shadow" : "Active")
                            + "  Auto: " + diagnostics.autoProfileSwitch()
                            + "  Quality: " + qualityLabel(diagnostics.rollbackRate()),
                    diagnostics.shadowMode() ? 0xFFD27F : qualityColor
            ));
        }
        if (config.hudShowServer()) {
            lines.add(new HudLine(
                    "Server: " + trimServerId(diagnostics.serverId()),
                    0x9ED0FF
            ));
        }

        int panelHeight = panelHeight(lines.size(), config.hudShowQualityBar());

        return new HudPresentationModel(
                lines,
                rollbackPercent,
                qualityColor,
                panelHeight,
                config.hudShowQualityBar()
        );
    }

    /**
     * Calculates panel height from configuration only.
     *
     * @param config runtime HUD configuration
     * @return panel height in pixels
     */
    public static int panelHeight(JumpRuntimeConfig config) {
        Objects.requireNonNull(config, "config");
        int visibleLineCount = 0;
        if (config.hudShowProfileAndPing()) {
            visibleLineCount++;
        }
        if (config.hudShowRollbackAndPenalty()) {
            visibleLineCount++;
        }
        if (config.hudShowModeAndQuality()) {
            visibleLineCount++;
        }
        if (config.hudShowServer()) {
            visibleLineCount++;
        }
        return panelHeight(visibleLineCount, config.hudShowQualityBar());
    }

    /**
     * Derives the quality color for a rollback rate.
     *
     * @param rollbackRate rollback ratio in [0, 1]
     * @return packed RGB color
     */
    public static int qualityColor(double rollbackRate) {
        if (rollbackRate >= 0.35D) {
            return QUALITY_HIGH_RISK_COLOR;
        }
        if (rollbackRate >= 0.15D) {
            return QUALITY_MODERATE_COLOR;
        }
        return QUALITY_STABLE_COLOR;
    }

    private static int rollbackPercent(double rollbackRate) {
        return (int) Math.round(Math.min(1.0D, Math.max(0.0D, rollbackRate)) * 100.0D);
    }

    private static int panelHeight(int visibleLineCount, boolean showQualityBar) {
        return PANEL_BASE_HEIGHT + (visibleLineCount * LINE_HEIGHT) + (showQualityBar ? QUALITY_BAR_HEIGHT : 0);
    }

    private static String qualityLabel(double rollbackRate) {
        if (rollbackRate >= 0.35D) {
            return "High Risk";
        }
        if (rollbackRate >= 0.15D) {
            return "Moderate";
        }
        return "Stable";
    }

    private static String trimServerId(String serverId) {
        if (serverId == null || serverId.isBlank()) {
            return "n/a";
        }
        if (serverId.length() <= SERVER_ID_LIMIT) {
            return serverId;
        }
        return serverId.substring(0, SERVER_ID_LIMIT) + "...";
    }
}
