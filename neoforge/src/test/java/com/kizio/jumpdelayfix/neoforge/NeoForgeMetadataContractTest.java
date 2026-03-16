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
        String metadata = readResource("/META-INF/mods.toml");

        assertFalse(metadata.contains("${"), "mods.toml still contains unresolved placeholders");
        assertTrue(metadata.contains("[[mods]]"));
        assertTrue(metadata.contains("modId=\"jumpdelayfix\""));
        assertTrue(metadata.contains("displayTest=\"IGNORE_SERVER_VERSION\""));
        assertTrue(metadata.contains("[[dependencies.jumpdelayfix]]"));
        assertTrue(metadata.contains("modId=\"minecraft\""));
        assertTrue(metadata.contains("modId=\"jei\""));
        assertTrue(metadata.contains("side=\"CLIENT\""));
    }

    private static String readResource(String resourcePath) throws IOException {
        try (InputStream stream = NeoForgeMetadataContractTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Missing test resource: " + resourcePath);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
