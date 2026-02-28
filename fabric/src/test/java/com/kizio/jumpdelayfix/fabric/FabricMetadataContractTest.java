package com.kizio.jumpdelayfix.fabric;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricMetadataContractTest {

    @Test
    void shouldDeclareClientOnlyEnvironmentAndEntrypoint() throws IOException {
        String metadata = readResource("/fabric.mod.json");

        assertFalse(metadata.contains("${"), "fabric.mod.json still contains unresolved placeholders");
        assertTrue(metadata.contains("\"environment\": \"client\""));
        assertTrue(metadata.contains("\"client\": ["));
        assertTrue(metadata.contains("com.kizio.jumpdelayfix.fabric.JumpDelayFixFabric"));
        assertTrue(metadata.contains("\"minecraft\":"));
        assertTrue(metadata.contains("\"fabric-api\":"));
    }

    private static String readResource(String resourcePath) throws IOException {
        try (InputStream stream = FabricMetadataContractTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Missing test resource: " + resourcePath);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
