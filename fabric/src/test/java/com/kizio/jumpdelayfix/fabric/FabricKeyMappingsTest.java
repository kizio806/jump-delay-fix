package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.client.input.KeyMappingAssertions;
import com.kizio.jumpdelayfix.fabric.client.ModMenuIntegration;
import com.kizio.jumpdelayfix.fabric.client.gui.FabricSettingsScreen;
import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FabricKeyMappingsTest {

    @Test
    void shouldInitializeKeyMappingsAndProvideSafeDefaults() {
        FabricKeyMappings mappings = FabricKeyMappings.getInstance();
        assertNotNull(mappings);
        KeyMappingAssertions.assertSafeDefaults(mappings);
    }

    @Test
    void shouldInstantiateJumpInput() {
        FabricJumpInput jumpInput = new FabricJumpInput();
        assertNotNull(jumpInput);
    }

    @Test
    void shouldProvideModMenuConfigScreen() {
        ModMenuIntegration integration = new ModMenuIntegration();
        assertNotNull(integration.getModConfigScreenFactory());
    }

    @Test
    void shouldInstantiateFabricEntrypoint() {
        JumpDelayFixFabric entrypoint = new JumpDelayFixFabric();
        assertNotNull(entrypoint);
    }
}
