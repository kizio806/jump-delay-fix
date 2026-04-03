package com.kizio.jumpdelayfix.fabric;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricMetadataContractTest {

    @Test
    void shouldDeclareClientOnlyEnvironmentAndEntrypoint() throws IOException {
        String metadata = Files.readString(Path.of("build", "resources", "main", "fabric.mod.json"), StandardCharsets.UTF_8);

        assertFalse(metadata.contains("${"), "fabric.mod.json still contains unresolved placeholders");
        assertTrue(metadata.contains("\"environment\": \"client\""));
        assertTrue(metadata.contains("\"client\": ["));
        assertTrue(metadata.contains("com.kizio.jumpdelayfix.fabric.JumpDelayFixFabric"));
        assertTrue(metadata.contains("\"minecraft\":"));
        assertTrue(metadata.contains("\"fabric-api\":"));
    }

}
