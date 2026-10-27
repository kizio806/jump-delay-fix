# Jump Delay Fix — Compatibility Matrix (support/26.1.x)

This document describes the compatibility and release specifications for the **`support/26.1.x`** branch of Jump Delay Fix.

> 🔗 For the canonical global compatibility matrix across all branches, see [compatibility-matrix.md on main](https://github.com/kizio806/jump-delay-fix/blob/main/docs/compatibility-matrix.md).

---

## 1. Branch Specifications

- **Branch:** `support/26.1.x`
- **Mod Version:** `1.2.2`
- **Target Minecraft Line:** `26.1.x` (`26.1`, `26.1.1`, `26.1.2`)
- **Default Compile Target:** `26.1.2`
- **Java Toolchain:** **Java 25**
- **Mappings:** Official Mojang mappings

---

## 2. Supported Loaders & Dependencies

Both Fabric and NeoForge runtime dependencies natively cover the entire 26.1 minor line (`>=26.1 <26.2`). Therefore, a unified release artifact per loader provides seamless out-of-the-box compatibility across all three versions without requiring separate JARs.

| Loader | Minecraft Versions | Dependency Versions | Artifact Name | Verification Status |
|---|---|---|---|---|
| **Fabric** | **26.1, 26.1.1, 26.1.2** | Fabric Loader `>=0.18.4`<br>Fabric API `0.155.2+26.1.2`<br>Range `>=26.1 <26.2`<br>ModMenu `18.0.0` | `jumpdelayfix-fabric-26.1.2-1.2.2.jar` | **VERIFIED (PASS, Probe PASS)** |
| **NeoForge** | **26.1, 26.1.1, 26.1.2** | NeoForge `26.1.2.103`<br>Range `[26.1,26.2)`<br>JavaFML `[4,)` | `jumpdelayfix-neoforge-26.1.2-1.2.2.jar` | **VERIFIED (PASS, Probe PASS)** |

---

## 3. Artifact Details & Checksums

| Artifact | Size | SHA-256 Checksum |
|---|---|---|
| `jumpdelayfix-fabric-26.1.2-1.2.2.jar` | ~243 KB | `fc2341e2240e65dfb18228456f54714c0450c74cab9ec8ec3a638ef251e2222c` |
| `jumpdelayfix-neoforge-26.1.2-1.2.2.jar` | ~242 KB | `7f12aa800f8bd42d3e88e287d3e6cbbde9189bbea62c4a1e64371780aa71f9d5` |

---

## 4. Verification Procedures

All three Minecraft versions (`26.1`, `26.1.1`, `26.1.2`) have been independently tested and confirmed:

```bash
./gradlew spotlessApply validateStructure validateSourceHygiene
./gradlew test --rerun
./gradlew strictCheck publishReadyCheck buildAll
./scripts/probe-minecraft-versions.sh --versions "26.1,26.1.1,26.1.2" --tasks "test publishReadyCheck"
```
