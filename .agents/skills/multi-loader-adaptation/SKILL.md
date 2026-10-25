---
name: multi-loader-adaptation
description: Implement, refactor, or debug loader-specific adapters across Fabric and NeoForge while maintaining loader independence in the common module.
---

# Multi-Loader Adaptation Skill

## When to use it
Use this skill when:
- Adding a new UI screen, widget, or configuration option that must exist on both Fabric and NeoForge.
- Adding or modifying keybindings.
- Adjusting how Minecraft player state (`onGround`, `getY`, `jumpFromGround`) is fetched.
- Fixing divergence or copy-paste inconsistencies between `fabric` and `neoforge` modules.

## Required steps
1. **Design Pure Java Interface in Common:**
   - Define or update the contract in `common/src/main/java/com/kizio/jumpdelayfix/input/` or `feedback/`.
   - Ensure the interface references no Minecraft or loader classes.
2. **Implement in Fabric Adapter:**
   - Implement the contract in `fabric/src/main/java/com/kizio/jumpdelayfix/fabric/client/`.
   - Use `@Environment(EnvType.CLIENT)` to mark client-only classes.
   - Use `ClientTickEvents`, `ClientLifecycleEvents`, or Fabric KeyBinding registry.
3. **Implement in NeoForge Adapter:**
   - Implement the contract in `neoforge/src/main/java/com/kizio/jumpdelayfix/neoforge/client/`.
   - Use `@OnlyIn(Dist.CLIENT)` to mark client-only classes.
   - Use `NeoForge.EVENT_BUS` or `modEventBus` listeners.
   - Guard entry points in `JumpDelayFixNeoForge` with `FMLEnvironment.getDist() == Dist.CLIENT`.
4. **Audit for Functional Parity:**
   - Verify that button placements, labels, tooltips, and click handlers in `FabricSettingsScreen` and `NeoForgeSettingsScreen` remain aligned.
   - Verify that `FabricJumpInput` and `NeoForgeJumpInput` use identical latency-to-tick mapping logic.
5. **Run Verification:**
   - Execute `./gradlew buildAll strictCheck` to ensure both loader artifacts compile and satisfy dedicated server safety.

## Analysis process
```text
1. Identify Common Domain Capability
   Determine what pure Java method needs Minecraft data or user action.
2. Interface Definition
   Update com.kizio.jumpdelayfix.input.JumpInput or appropriate common interface.
3. Dual Adapter Implementation
   Implement in fabric/... and neoforge/...
4. Contract & Server Safety Verification
   Run :neoforge:verifyDedicatedServerSafe and loader contract tests.
```

## Expected output
- Fully symmetric adapter code across `fabric/` and `neoforge/`.
- Zero loader imports in `common/`.
- Successful compilation for both Fabric and NeoForge JARs.

## Validation checklist
- [ ] No `net.fabricmc.*` or `net.neoforged.*` imports in `common`.
- [ ] Both `fabric` and `neoforge` screens have identical UI features.
- [ ] Keybinding defaults and categories match in `FabricKeyMappings` and `NeoForgeKeyMappings`.
- [ ] Dedicated server safety check passes without violations.
