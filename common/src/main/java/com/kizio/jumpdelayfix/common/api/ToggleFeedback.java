package com.kizio.jumpdelayfix.common.api;

/**
 * Receives user-facing feedback whenever the mod enabled state changes.
 */
@FunctionalInterface
public interface ToggleFeedback {

    ToggleFeedback NO_OP = enabled -> {
    };

    /**
     * Called after toggling enabled state.
     *
     * @param enabled resulting enabled state
     */
    void onToggle(boolean enabled);
}
