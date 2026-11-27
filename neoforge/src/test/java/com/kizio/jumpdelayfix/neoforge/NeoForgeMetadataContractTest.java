package com.kizio.jumpdelayfix.neoforge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

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
        assertTrue(metadata.contains("side=\"CLIENT\""));
        assertFalse(metadata.contains("side=\"BOTH\""));
        assertFalse(metadata.contains("modId=\"jei\""));
        assertFalse(metadata.contains("modId=\"emi\""));
    }

    @Test
    void shouldEnforcePlayerStateChecksInBytecodeContract() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/com/kizio/jumpdelayfix/client/input/ClientJumpInput.class")) {
            assertNotNull(stream, "Missing ClientJumpInput class");
            ClassReader reader = new ClassReader(stream);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            Set<String> invokedMethods = new HashSet<>();
            Set<String> accessedFields = new HashSet<>();
            for (MethodNode method : classNode.methods) {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode methodInsn) {
                        invokedMethods.add(methodInsn.name);
                    } else if (insn instanceof FieldInsnNode fieldInsn) {
                        accessedFields.add(fieldInsn.name);
                    }
                }
            }

            assertTrue(invokedMethods.contains("isInWater"));
            assertTrue(invokedMethods.contains("isSwimming"));
            assertTrue(invokedMethods.contains("isVisuallyCrawling"));
            assertTrue(invokedMethods.contains("isPassenger"));
            assertTrue(invokedMethods.contains("isFallFlying"));
            assertTrue(invokedMethods.contains("onClimbable"));
            assertTrue(invokedMethods.contains("screen") || accessedFields.contains("screen"), "ClientJumpInput must check screen suppression");
        }
    }

    @Test
    void shouldRenderBackgroundInSettingsScreen() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/com/kizio/jumpdelayfix/client/gui/SettingsScreen.class")) {
            assertNotNull(stream, "Missing SettingsScreen class");
            ClassReader reader = new ClassReader(stream);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            Set<String> invokedMethods = new HashSet<>();
            for (MethodNode method : classNode.methods) {
                if (method.name.equals("extractRenderState")) {
                    for (AbstractInsnNode insn : method.instructions) {
                        if (insn instanceof MethodInsnNode methodInsn) {
                            invokedMethods.add(methodInsn.name);
                        }
                    }
                }
            }
            assertTrue(invokedMethods.contains("extractBackground"), "SettingsScreen must call extractBackground to prevent bleed-through");
        }
    }

    private static String readResource(String resourcePath) throws IOException {
        try (InputStream stream = NeoForgeMetadataContractTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Missing test resource: " + resourcePath);
            return new String(stream.readAllBytes(), StandardCharsets.ISO_8859_1);
        }
    }
}
