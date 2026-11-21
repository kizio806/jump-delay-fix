package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FabricKeyMappingsTest {

    @Test
    void shouldInitializeKeyMappingsAndProvideSafeDefaults() {
        FabricKeyMappings mappings = FabricKeyMappings.getInstance();
        assertNotNull(mappings);

        assertFalse(mappings.consumeTogglePress());
        assertFalse(mappings.consumeProfileCyclePress());
        assertFalse(mappings.consumeConfigScreenPress());
    }

    @Test
    void shouldInstantiateJumpInput() {
        FabricJumpInput jumpInput = new FabricJumpInput();
        assertNotNull(jumpInput);
    }
}
