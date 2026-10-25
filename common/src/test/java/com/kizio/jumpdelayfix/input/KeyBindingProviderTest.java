package com.kizio.jumpdelayfix.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyBindingProviderTest {

    @Test
    void shouldReturnDefaultValuesForNoOpKeyBindingProvider() {
        KeyBindingProvider provider = KeyBindingProvider.NO_OP;

        assertFalse(provider.consumeTogglePress());
        assertFalse(provider.consumeProfileCyclePress());
        assertFalse(provider.consumeConfigScreenPress());
    }

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

        assertEquals(1, defaultInput.requiredGroundedTicksBeforeJump());
        assertTrue(Double.isNaN(defaultInput.getPlayerY()));
        assertEquals(-1, defaultInput.getLatencyMs());
        assertEquals("global", defaultInput.getServerIdentifier());
    }
}
