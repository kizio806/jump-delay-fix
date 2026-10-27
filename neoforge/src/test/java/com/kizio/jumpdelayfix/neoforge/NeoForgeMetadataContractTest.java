package com.kizio.jumpdelayfix.neoforge;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeoForgeMetadataContractTest {

    @Test
    void shouldDeclareProductionMetadataContract() throws IOException {
        String metadata = readResource("/META-INF/neoforge.mods.toml");

        assertFalse(metadata.contains("${"), "neoforge.mods.toml still contains unresolved placeholders");
        assertTrue(metadata.contains("[[mods]]"));
        assertTrue(metadata.contains("modId=\"jumpdelayfix\""));
        assertTrue(metadata.contains("displayTest=\"IGNORE_SERVER_VERSION\""));
        assertTrue(metadata.contains("[[dependencies.jumpdelayfix]]"));
        assertTrue(metadata.contains("modId=\"neoforge\""));
        assertTrue(metadata.contains("modId=\"minecraft\""));
        assertFalse(metadata.contains("modId=\"jei\""));
        assertFalse(metadata.contains("modId=\"emi\""));
    }

    @Test
    void shouldEnforcePlayerStateChecksInBytecodeContract() throws IOException {
        String bytecode = readResource("/com/kizio/jumpdelayfix/neoforge/client/input/NeoForgeJumpInput.class");
        assertTrue(bytecode.contains("isInWater"));
        assertTrue(bytecode.contains("isSwimming"));
        assertTrue(bytecode.contains("isVisuallyCrawling"));
        assertTrue(bytecode.contains("isPassenger"));
        assertTrue(bytecode.contains("isFallFlying"));
    }

    private static String readResource(String resourcePath) throws IOException {
        try (InputStream stream = NeoForgeMetadataContractTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Missing test resource: " + resourcePath);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
