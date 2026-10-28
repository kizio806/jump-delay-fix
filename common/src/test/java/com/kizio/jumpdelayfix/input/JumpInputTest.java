package com.kizio.jumpdelayfix.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpInputTest {

    @Test
    void shouldReturnDefaultValuesForJumpInputDefaults() {
        JumpInput defaultInput = new JumpInput() {
            @Override
            public boolean isJumpPressed() {
                return false;
            }

            @Override
            public boolean isPlayerOnGround() {
                return false;
            }

            @Override
            public void jump() {
            }
        };

        assertTrue(defaultInput.isPlayerValid());
        assertEquals(1, defaultInput.requiredGroundedTicksBeforeJump());
        assertTrue(Double.isNaN(defaultInput.getPlayerY()));
        assertEquals(-1, defaultInput.getLatencyMs());
        assertEquals("global", defaultInput.getServerIdentifier());
    }
}
