package com.kizio.jumpdelayfix.feedback;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ToggleFeedbackTest {

    @Test
    void shouldExecuteNoOpFeedbackWithoutException() {
        ToggleFeedback feedback = ToggleFeedback.NO_OP;
        assertDoesNotThrow(() -> feedback.onToggle(true));
        assertDoesNotThrow(() -> feedback.onToggle(false));
    }
}
