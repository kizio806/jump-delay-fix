package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.client.input.KeyMappingAssertions;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeJumpInput;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeKeyMappings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NeoForgeKeyMappingsTest {

    @Test
    void shouldInitializeKeyMappingsAndProvideSafeDefaults() {
        NeoForgeKeyMappings mappings = NeoForgeKeyMappings.getInstance();
        assertNotNull(mappings);
        KeyMappingAssertions.assertSafeDefaults(mappings);
    }

    @Test
    void shouldInstantiateJumpInput() {
        NeoForgeJumpInput jumpInput = new NeoForgeJumpInput();
        assertNotNull(jumpInput);
    }
}
