package com.kizio.jumpdelayfix.fabric;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

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
        assertFalse(metadata.contains("\"rei\""));
        assertFalse(metadata.contains("\"emi\""));
    }

    @Test
    void shouldEnforcePlayerStateChecksInBytecodeContract() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/com/kizio/jumpdelayfix/client/input/ClientJumpInput.class")) {
            assertNotNull(stream, "Missing ClientJumpInput class");
            ClassReader reader = new ClassReader(stream);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            Set<String> invokedMethods = new HashSet<>();
            for (MethodNode method : classNode.methods) {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode methodInsn) {
                        invokedMethods.add(methodInsn.name);
                    }
                }
            }

            assertTrue(invokedMethods.contains("isInWater"));
            assertTrue(invokedMethods.contains("isSwimming"));
            assertTrue(invokedMethods.contains("isVisuallyCrawling"));
            assertTrue(invokedMethods.contains("isPassenger"));
            assertTrue(invokedMethods.contains("isFallFlying"));
        }
    }

}
