package com.kizio.jumpdelayfix.common.client.hud;

import java.util.List;
import java.util.Objects;

/**
 * Loader-agnostic HUD view model.
 *
 * @param lines          ordered text lines to render below the header
 * @param rollbackPercent rollback represented as an integer percentage
 * @param qualityColor   quality color derived from rollback severity
 * @param panelHeight    full panel height in pixels for the current configuration
 * @param showQualityBar whether the quality bar should be rendered
 */
public record HudPresentationModel(
        List<HudLine> lines,
        int rollbackPercent,
        int qualityColor,
        int panelHeight,
        boolean showQualityBar
) {

    public HudPresentationModel {
        Objects.requireNonNull(lines, "lines");
        lines = List.copyOf(lines);
    }

    /**
     * Calculates the filled width of the quality bar for a given bar width.
     *
     * @param totalWidth full bar width in pixels
     * @return filled width in pixels
     */
    public int qualityBarFilledWidth(int totalWidth) {
        return (int) Math.round((rollbackPercent / 100.0D) * totalWidth);
    }
}
