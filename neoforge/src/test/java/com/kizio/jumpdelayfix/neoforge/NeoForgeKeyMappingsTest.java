package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeJumpInput;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeKeyMappings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NeoForgeKeyMappingsTest {

    @Test
    void shouldInitializeKeyMappingsAndProvideSafeDefaults() {
        NeoForgeKeyMappings mappings = NeoForgeKeyMappings.getInstance();
        assertNotNull(mappings);

        assertFalse(mappings.consumeTogglePress());
        assertFalse(mappings.consumeProfileCyclePress());
        assertFalse(mappings.consumeConfigScreenPress());
    }

    @Test
    void shouldInstantiateJumpInput() {
        NeoForgeJumpInput jumpInput = new NeoForgeJumpInput();
        assertNotNull(jumpInput);
    }
}
