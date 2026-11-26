# Jump Delay Fix — Compatibility Matrix

This document is the **canonical compatibility reference** for the Jump Delay Fix project across all actively maintained branches and supported Minecraft versions.

Last verified: **2026-10-27** | Verification toolchain: Gradle 8.14/9.7/9.8, OpenJDK 21 & 25.

---

## 1. Active Development & Support Branches

| Branch | Minecraft Line | Mod Version | Default Compile Target | Java Baseline | Artifact Strategy | Status |
|---|---|---|---|---|---|---|
| [`main`](https://github.com/kizio806/jump-delay-fix) | **26.3** | `2.0.0` | Minecraft 26.3 | **Java 25** | Single artifact per loader | **Active (Primary)** |
| [`support/26.2.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.2.x) | **26.2** | `2.0.0` | Minecraft 26.2 | **Java 25** | Single artifact per loader | **LTS Support** |
| [`support/26.1.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.1.x) | **26.1.x** (26.1 – 26.1.2) | `2.0.0` | Minecraft 26.1.2 | **Java 25** | Unified line artifact per loader | **LTS Support** |
| [`support/1.21.9-1.21.11`](https://github.com/kizio806/jump-delay-fix/tree/support/1.21.9-1.21.11) | **1.21.9 – 1.21.11** | `2.0.0` | Minecraft 1.21.11 | **Java 21** | Unified line artifact per loader | **LTS Support** |
| [`support/1.21.0-1.21.8`](https://github.com/kizio806/jump-delay-fix/tree/support/1.21.0-1.21.8) | **1.21.0 – 1.21.8** | `2.0.0` | Minecraft 1.21.8 | **Java 21** | Unified line artifact per loader | **LTS Support** |
| [`support/1.20.5-1.20.6`](https://github.com/kizio806/jump-delay-fix/tree/support/1.20.5-1.20.6) | **1.20.5 – 1.20.6** | `2.0.0` | Minecraft 1.20.6 | **Java 21** | Unified line artifact per loader | **LTS Support** |

---

## 2. Complete Combination Matrix & Verification Results

Every declared combination of Mod Loader and Minecraft version is independently compiled, probed, tested, and verified against strict release gates (`test`, `publishReadyCheck`, `strictCheck`, dedicated server safety).

| # | Branch | Mod Loader | Minecraft Version | Dependency Versions | Compilation & Tests | Production Artifact | Verification Status |
|---|---|---|---|---|---|---|---|
| 1 | `main` | **Fabric** | **26.3** | Fabric API `0.160.6+26.3`<br>Fabric Loader `>=0.18.4` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.3.x-fabric.jar` | **VERIFIED** |
| 2 | `main` | **NeoForge** | **26.3** | NeoForge `26.3.0.1-beta`<br>Loader `[26.3,26.4)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.3.x-neoforge.jar` | **VERIFIED** |
| 3 | `support/26.2.x` | **Fabric** | **26.2** | Fabric API `0.159.0+26.2`<br>Fabric Loader `>=0.18.4` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.2.x-fabric.jar` | **VERIFIED** |
| 4 | `support/26.2.x` | **NeoForge** | **26.2** | NeoForge `26.2.0.79`<br>Loader `[26.2,26.3)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.2.x-neoforge.jar` | **VERIFIED** |
| 5 | `support/26.1.x` | **Fabric** | **26.1** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-fabric.jar` | **VERIFIED** |
| 6 | `support/26.1.x` | **Fabric** | **26.1.1** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-fabric.jar` | **VERIFIED** |
| 7 | `support/26.1.x` | **Fabric** | **26.1.2** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-fabric.jar` | **VERIFIED** |
| 8 | `support/26.1.x` | **NeoForge** | **26.1** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-neoforge.jar` | **VERIFIED** |
| 9 | `support/26.1.x` | **NeoForge** | **26.1.1** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-neoforge.jar` | **VERIFIED** |
| 10 | `support/26.1.x` | **NeoForge** | **26.1.2** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc26.1.x-neoforge.jar` | **VERIFIED** |
| 11 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.9** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-fabric.jar` | **VERIFIED** |
| 12 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.10** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-fabric.jar` | **VERIFIED** |
| 13 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.11** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-fabric.jar` | **VERIFIED** |
| 14 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.9** | NeoForge `21.9.16+`<br>Range `[21.9,21.12)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-neoforge.jar` | **VERIFIED** |
| 15 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.10** | NeoForge `21.10.64+`<br>Range `[21.9,21.12)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-neoforge.jar` | **VERIFIED** |
| 16 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.11** | NeoForge `21.11.48+`<br>Range `[21.9,21.12)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.9-1.21.11-neoforge.jar` | **VERIFIED** |
| 17 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.0** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 18 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.1** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 19 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.2** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 20 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.3** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 21 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.4** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 22 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.5** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 23 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.6** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 24 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.7** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 25 | `support/1.21.0-1.21.8` | **Fabric** | **1.21.8** | Fabric API `>=0.100.8`<br>Range `>=1.21.0 <1.21.9` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar` | **VERIFIED** |
| 26 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.0** | NeoForge `21.0.167+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 27 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.1** | NeoForge `21.1.267+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 28 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.2** | NeoForge `21.2.x`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 29 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.3** | NeoForge `21.3.100+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 30 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.4** | NeoForge `21.4.161+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 31 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.5** | NeoForge `21.5.101+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 32 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.6** | NeoForge `21.6.x`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 33 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.7** | NeoForge `21.7.25-beta+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 34 | `support/1.21.0-1.21.8` | **NeoForge** | **1.21.8** | NeoForge `21.8.57+`<br>Range `[21.0,21.9)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar` | **VERIFIED** |
| 35 | `support/1.20.5-1.20.6` | **Fabric** | **1.20.5** | Fabric API `>=0.97.7`<br>Range `>=1.20.5 <1.20.7` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.20.5-1.20.6-fabric.jar` | **VERIFIED** |
| 36 | `support/1.20.5-1.20.6` | **Fabric** | **1.20.6** | Fabric API `>=0.97.7`<br>Range `>=1.20.5 <1.20.7` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.20.5-1.20.6-fabric.jar` | **VERIFIED** |
| 37 | `support/1.20.5-1.20.6` | **NeoForge** | **1.20.5** | NeoForge `20.5.21-beta+`<br>Range `[20.5,20.7)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.20.5-1.20.6-neoforge.jar` | **VERIFIED** |
| 38 | `support/1.20.5-1.20.6` | **NeoForge** | **1.20.6** | NeoForge `20.6.144`<br>Range `[20.5,20.7)` | PASS (`-Werror`) | `jump-delay-fix-2.0.0-mc1.20.5-1.20.6-neoforge.jar` | **VERIFIED** |

---

## 3. Architecture & Technical Boundary Justification

### Minecraft 1.20.5/1.20.6 vs 1.20.4 Hard Binary Boundary
A separate release line is required between Minecraft 1.20.4 and 1.20.5 due to runtime and platform architecture breaks:
1. **Java Runtime Requirement:**
   - Minecraft 1.20.5 raised the Java runtime requirement to **Java 21** (class bytecode version 65).
   - Minecraft 1.20.4 and earlier vanilla installations execute on **Java 17** (class bytecode version 61). Bytecode compiled for Java 21 causes `UnsupportedClassVersionError` on 1.20.4 clients.
2. **NeoForge Event Model Architecture:**
   - NeoForge 20.6 replaced legacy `TickEvent.ClientTickEvent` with `net.neoforged.neoforge.client.event.ClientTickEvent.Post`. NeoForge 20.4 lacks `ClientTickEvent.Post`, while NeoForge 20.6 removed `TickEvent`.
3. **Internal Component Rewrite:**
   - Minecraft 1.20.5 underwent a major engine rewrite introducing the item component architecture.
4. **Conclusion:** Unifying 1.20.4 and 1.20.5 into a single binary is impossible without unsafe reflection or fragile multi-release tricks. `1.20.5–1.20.6` constitutes a natural, unified binary compatibility boundary.

### Minecraft 1.21.8 vs 1.21.9 Hard Binary Boundary
A separate release line is required between Minecraft 1.21.8 and 1.21.9 due to breaking upstream API and platform changes:
1. **KeyBinding/KeyMapping Category API:**
   - In Minecraft 1.21.9 ("The Copper Age"), Mojang introduced typed categories: `KeyBinding.Category` (Fabric) and `KeyMapping.Category` (NeoForge), replacing `String category` parameters in `KeyBinding`/`KeyMapping` constructors (see [Minecraft 1.21.9 Snapshot / Patch Notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21) and [Fabric KeyBinding Documentation](https://fabricmc.net/wiki/tutorial:keybinds)).
   - Code compiled against 1.21.9+ produces bytecode expecting `(Ljava/lang/String;ILnet/minecraft/client/option/KeyBinding$Category;)V`. Running this on 1.21.8 or earlier throws `NoSuchMethodError` or `NoSuchFieldError` because the runtime constructor expects `(Ljava/lang/String;ILjava/lang/String;)V`.
   - Conversely, code compiled against 1.21.8 using `String category` fails on 1.21.9+ when loaders drop legacy constructor overloads.
2. **NeoForge FMLEnvironment Dist Access:**
   - In NeoForge 21.9+, `FMLEnvironment.getDist()` method was introduced and direct public field access to `FMLEnvironment.dist` was restricted/deprecated (see [NeoForge Pull Request #326 / FMLEnvironment accessors](https://github.com/neoforged/NeoForge/pull/326)).
   - Older NeoForge versions (21.0–21.8) do not expose `getDist()`, throwing `NoSuchMethodError` if invoked.
3. **Conclusion:** Merging 1.21.8 and 1.21.9 into a single binary is impossible without unsafe reflection or fragile multi-release tricks. Thus, **1.21.9–1.21.11** and **1.21.0–1.21.8** represent the two natural, mathematically minimal release boundaries covering the 1.21.x series.

### Unified 1.21.0–1.21.8 Single Release Architecture
The entire range from 1.21.0 through 1.21.8 is packaged into **exactly ONE release**:
1. **Fabric Yarn Mapping Evolution & Intermediary Parity:**
   - In Yarn mappings for 1.21.0–1.21.1, `method_6128` was named `isFallFlying()`.
   - In Yarn mappings for 1.21.2+, it was renamed to `isGliding()`.
   - Fabric Loom compiles against Yarn mappings and remaps all calls to standard Fabric intermediary names at build time. Both `isFallFlying()` and `isGliding()` remap to the exact same descriptor in [Fabric Intermediary](https://github.com/FabricMC/intermediary): `net.minecraft.class_1309.method_6128()Z`.
   - Therefore, compiling against Yarn 1.21.8 (`isGliding()`) generates bytecode that executes identically across all 1.21.0 through 1.21.8 Fabric environments.
2. **NeoForge Binary Compatibility:**
   - Throughout NeoForge 21.0 (`21.0.167`) to NeoForge 21.8 (`21.8.57`), the Mojang method `player.isFallFlying()` (`m_21255_()`), `KeyMapping("<init>", String, int, String)`, and `FMLEnvironment.dist == Dist.CLIENT` maintain 100% binary compatibility.
3. **Fabric API Minimum Version:**
   - Fabric API `0.100.8` is the baseline required for 1.21.0, and is fully backward and forward compatible through 1.21.8 (see [Fabric API Release Matrix](https://fabricmc.net/develop/)).

### Mapping Differences Across Major Lines
- **Minecraft 26.x (`main`, `26.2.x`, `26.1.x`):**
  Uses official Mojang mappings natively provided by Fabric Loom and NeoForge ModDev.
  Method identifiers: `player.jumpFromGround()`, `player.isInWater()`, `player.isSwimming()`, `player.isVisuallyCrawling()`, `player.isPassenger()`, `player.isFallFlying()`.
- **Minecraft 1.21.x (`support/1.21.9-1.21.11` & `support/1.21.0-1.21.8`):**
  Fabric uses Yarn mappings (`net.fabricmc.yarn`), where client methods map to `player.jump()`, `player.isTouchingWater()`, `player.isSwimming()`, `player.isCrawling()`, `player.hasVehicle()`, `player.isGliding()`. NeoForge uses official Mojang mappings.
  Bytecode contract tests in both loaders verify that the exact mapped bytecode invokes the intended player physics predicates.

### Artifact Packaging Strategy
Across all branches, Jump Delay Fix strictly adheres to the **single-JAR per loader standard**:
1. **Single-Target Lines (`26.3`, `26.2`):**
   One production JAR per loader (`jump-delay-fix-2.0.0-mc<ver>-fabric.jar` and `jump-delay-fix-2.0.0-mc<ver>-neoforge.jar`).
2. **Unified Minor Lines (`26.1.x`, `1.21.9–1.21.11`, `1.21.0–1.21.8`):**
   Multi-patch lines use broad, verified version ranges (`>=26.1 <26.2` on 26.1.x; `>=1.21.9 <1.21.12` on 1.21.9-1.21.11; `>=1.21.0 <1.21.9` on 1.21.0-1.21.8). Exactly one unified JAR per loader satisfies the entire patch line without runtime conflicts, launcher confusion, or duplicate releases.

---

## 4. Verification Procedures

To reproduce verification for any branch:

```bash
# Verify formatting, source hygiene, and structure
./gradlew spotlessApply validateStructure validateSourceHygiene

# Run full unit and integration test suite
./gradlew test --rerun

# Run strict release qualification gates
./gradlew strictCheck publishReadyCheck buildAll

# Run multi-version probe (for branches with multiple versions)
./scripts/probe-minecraft-versions.sh --versions "<comma_separated_versions>" --tasks "test publishReadyCheck"
```
