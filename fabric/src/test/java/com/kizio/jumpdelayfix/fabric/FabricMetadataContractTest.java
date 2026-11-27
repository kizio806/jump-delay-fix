package com.kizio.jumpdelayfix.fabric;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class FabricMetadataContractTest {

    @Test
    void shouldDeclareClientOnlyEnvironmentAndEntrypoint() throws IOException {
        String metadata;
        try (InputStream stream = getClass().getResourceAsStream("/fabric.mod.json")) {
            assertNotNull(stream, "Missing fabric.mod.json on test classpath");
            metadata = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertFalse(metadata.contains("${"), "fabric.mod.json still contains unresolved placeholders");
        assertTrue(metadata.contains("\"environment\": \"client\""));
        assertTrue(metadata.contains("\"client\": ["));
        assertTrue(metadata.contains("com.kizio.jumpdelayfix.fabric.JumpDelayFixFabric"));
        assertTrue(metadata.contains("\"minecraft\":"));
        assertTrue(metadata.contains("\"fabric-api\":"));
        assertFalse(metadata.contains("\"rei\""));
        assertFalse(metadata.contains("\"emi\""));
    }

    @Test
    void shouldEnforcePlayerStateChecksInBytecodeContract() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/com/kizio/jumpdelayfix/fabric/client/input/FabricJumpInput.class")) {
            assertNotNull(stream, "Missing FabricJumpInput class");
            String bytecode = new String(stream.readAllBytes(), StandardCharsets.ISO_8859_1);
            assertTrue(bytecode.contains("isTouchingWater"));
            assertTrue(bytecode.contains("isSwimming"));
            assertTrue(bytecode.contains("isCrawling"));
            assertTrue(bytecode.contains("hasVehicle"));
            assertTrue(bytecode.contains("isGliding"));
            assertTrue(bytecode.contains("isClimbing"));
            assertTrue(bytecode.contains("currentScreen"));
        }
    }
}
