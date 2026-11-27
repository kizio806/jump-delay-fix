package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeJumpInput;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeKeyMappings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NeoForgeKeyMappingsTest {

    @Test
    void shouldInitializeKeyMappingsAndProvideSafeDefaults() {
        NeoForgeKeyMappings mappings = NeoForgeKeyMappings.getInstance();
        assertNotNull(mappings);
        assertFalse(mappings.consumeTogglePress(), "Toggle press must default to unpressed");
        assertFalse(mappings.consumeProfileCyclePress(), "Profile cycle press must default to unpressed");
        assertFalse(mappings.consumeConfigScreenPress(), "Config screen press must default to unpressed");
    }

    @Test
    void shouldInstantiateJumpInput() {
        NeoForgeJumpInput jumpInput = new NeoForgeJumpInput();
        assertNotNull(jumpInput);
    }

    @Test
    void shouldValidateModConstructorArguments() {
        assertThrows(NullPointerException.class, () -> new JumpDelayFixNeoForge(null, null));
    }
}
