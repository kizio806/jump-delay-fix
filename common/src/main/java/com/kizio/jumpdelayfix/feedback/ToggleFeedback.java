package com.kizio.jumpdelayfix.feedback;
@FunctionalInterface
public interface ToggleFeedback {
    ToggleFeedback NO_OP = enabled -> {
    };
    void onToggle(boolean enabled);
}
