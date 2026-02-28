package com.kizio.jumpdelayfix.common.client.hud;

import java.util.Objects;

/**
 * Immutable line payload rendered by loader-specific HUD adapters.
 *
 * @param text  already formatted line text
 * @param color RGB color used for rendering
 */
public record HudLine(String text, int color) {

    public HudLine {
        Objects.requireNonNull(text, "text");
    }
}
