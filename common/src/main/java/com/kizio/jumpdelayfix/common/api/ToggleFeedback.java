package com.kizio.jumpdelayfix.common.api;

@FunctionalInterface
public interface ToggleFeedback {

    ToggleFeedback NO_OP = enabled -> {
    };

    void onToggle(boolean enabled);
}
