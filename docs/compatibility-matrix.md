# Jump Delay Fix — Compatibility Matrix

This document is the **canonical compatibility reference** for the Jump Delay Fix project across all actively maintained branches and supported Minecraft versions.

Last verified: **2026-10-27** | Verification toolchain: Gradle 8.14/9.7/9.8, OpenJDK 21 & 25.

---

## 1. Active Development & Support Branches

| Branch | Minecraft Line | Mod Version | Default Compile Target | Java Baseline | Artifact Strategy | Status |
|---|---|---|---|---|---|---|
| [`main`](https://github.com/kizio806/jump-delay-fix) | **26.3** | `1.4.2` | Minecraft 26.3 | **Java 25** | Single artifact per loader | **Active (Primary)** |
| [`support/26.2.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.2.x) | **26.2** | `1.3.2` | Minecraft 26.2 | **Java 25** | Single artifact per loader | **LTS Support** |
| [`support/26.1.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.1.x) | **26.1.x** (26.1 – 26.1.2) | `1.2.2` | Minecraft 26.1.2 | **Java 25** | Unified line artifact per loader | **LTS Support** |
| [`support/1.21.9-1.21.11`](https://github.com/kizio806/jump-delay-fix/tree/support/1.21.9-1.21.11) | **1.21.9 – 1.21.11** | `1.1.2` | Minecraft 1.21.11 | **Java 21** | Unified line artifact per loader | **LTS Support** |

---

## 2. Complete 16-Combination Matrix & Verification Results

Every declared combination of Mod Loader and Minecraft version is independently compiled, probed, tested, and verified against strict release gates (`test`, `publishReadyCheck`, `strictCheck`, dedicated server safety).

| # | Branch | Mod Loader | Minecraft Version | Dependency Versions | Compilation & Tests | Production Artifact | SHA-256 Checksum (first 16 hex) | Verification Status |
|---|---|---|---|---|---|---|---|---|
| 1 | `main` | **Fabric** | **26.3** | Fabric API `0.160.6+26.3`<br>Fabric Loader `>=0.18.4` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-26.3-1.4.2.jar` | `3ff13a848c4c976e` | **VERIFIED (Probe PASS)** |
| 2 | `main` | **NeoForge** | **26.3** | NeoForge `26.3.0.1-beta`<br>Loader `[26.3,26.4)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-26.3-1.4.2.jar` | `94888a30873175bd` | **VERIFIED (Probe PASS)** |
| 3 | `support/26.2.x` | **Fabric** | **26.2** | Fabric API `0.159.0+26.2`<br>Fabric Loader `>=0.18.4` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-26.2-1.3.2.jar` | `9dd7560887480b62` | **VERIFIED (Probe PASS)** |
| 4 | `support/26.2.x` | **NeoForge** | **26.2** | NeoForge `26.2.0.79`<br>Loader `[26.2,26.3)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-26.2-1.3.2.jar` | `827e7fe61f3e096c` | **VERIFIED (Probe PASS)** |
| 5 | `support/26.1.x` | **Fabric** | **26.1** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-26.1.2-1.2.2.jar` | `fc2341e2240e65df` | **VERIFIED (Probe PASS)** |
| 6 | `support/26.1.x` | **Fabric** | **26.1.1** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-26.1.2-1.2.2.jar` | `fc2341e2240e65df` | **VERIFIED (Probe PASS)** |
| 7 | `support/26.1.x` | **Fabric** | **26.1.2** | Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-26.1.2-1.2.2.jar` | `fc2341e2240e65df` | **VERIFIED (Probe PASS)** |
| 8 | `support/26.1.x` | **NeoForge** | **26.1** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-26.1.2-1.2.2.jar` | `7f12aa800f8bd42d` | **VERIFIED (Probe PASS)** |
| 9 | `support/26.1.x` | **NeoForge** | **26.1.1** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-26.1.2-1.2.2.jar` | `7f12aa800f8bd42d` | **VERIFIED (Probe PASS)** |
| 10 | `support/26.1.x` | **NeoForge** | **26.1.2** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-26.1.2-1.2.2.jar` | `7f12aa800f8bd42d` | **VERIFIED (Probe PASS)** |
| 11 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.9** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-1.21.11-1.1.2.jar` | `d08a8dcb0fe1f548` | **VERIFIED (Probe PASS)** |
| 12 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.10** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-1.21.11-1.1.2.jar` | `d08a8dcb0fe1f548` | **VERIFIED (Probe PASS)** |
| 13 | `support/1.21.9-1.21.11` | **Fabric** | **1.21.11** | Fabric API `>=0.134.1`<br>Range `>=1.21.9 <1.21.12` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-fabric-1.21.11-1.1.2.jar` | `d08a8dcb0fe1f548` | **VERIFIED (Probe PASS)** |
| 14 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.9** | NeoForge `21.9.16+`<br>Range `[1.21.9,1.21.12)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-1.21.11-1.1.2.jar` | `2a3374c34c69616f` | **VERIFIED (Probe PASS)** |
| 15 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.10** | NeoForge `21.10.64+`<br>Range `[1.21.9,1.21.12)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-1.21.11-1.1.2.jar` | `2a3374c34c69616f` | **VERIFIED (Probe PASS)** |
| 16 | `support/1.21.9-1.21.11` | **NeoForge** | **1.21.11** | NeoForge `21.11.48+`<br>Range `[1.21.9,1.21.12)` | PASS (`-Werror`)<br>73/73 tests | `jumpdelayfix-neoforge-1.21.11-1.1.2.jar` | `2a3374c34c69616f` | **VERIFIED (Probe PASS)** |

---

## 3. Architecture & Mapping Notes

### Mapping Differences Between Major Lines
- **Minecraft 26.x (`main`, `26.2.x`, `26.1.x`):**
  Uses official Mojang mappings natively provided by Fabric Loom and NeoForge ModDev.
  Method identifiers: `player.jumpFromGround()`, `player.isInWater()`, `player.isSwimming()`, `player.isVisuallyCrawling()`, `player.isPassenger()`, `player.isFallFlying()`.
- **Minecraft 1.21.x (`support/1.21.9-1.21.11`):**
  Fabric uses Yarn mappings (`net.fabricmc.yarn`), where client methods map to `player.jump()`, `player.isTouchingWater()`, `player.isSwimming()`, `player.isCrawling()`, `player.hasVehicle()`, `player.isGliding()`. NeoForge uses official Mojang mappings.
  Bytecode contract tests in both loaders verify that the exact mapped bytecode invokes the intended player physics predicates.

### Artifact Packaging Strategy
Across all branches, Jump Delay Fix strictly adheres to the **single-JAR per loader standard**:
1. **Single-Target Lines (`26.3`, `26.2`):**
   One production JAR per loader (`jumpdelayfix-fabric-<ver>.jar` and `jumpdelayfix-neoforge-<ver>.jar`).
2. **Unified Minor Lines (`26.1.x`, `1.21.9–1.21.11`):**
   Multi-patch lines use broad, verified version ranges (`>=26.1 <26.2` on 26.1.x; `>=1.21.9 <1.21.12` on 1.21.x). Exactly one unified JAR per loader satisfies the entire patch line without runtime conflicts, launcher confusion, or duplicate releases.

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
