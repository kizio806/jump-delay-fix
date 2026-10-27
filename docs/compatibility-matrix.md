# Jump Delay Fix — Compatibility Matrix (support/26.2.x)

This document describes the compatibility and release specifications for the **`support/26.2.x`** branch of Jump Delay Fix.

> 🔗 For the canonical global compatibility matrix across all branches, see [compatibility-matrix.md on main](https://github.com/kizio806/jump-delay-fix/blob/main/docs/compatibility-matrix.md).

---

## 1. Branch Specifications

- **Branch:** `support/26.2.x`
- **Mod Version:** `1.3.2`
- **Target Minecraft Version:** `26.2`
- **Java Toolchain:** **Java 25**
- **Mappings:** Official Mojang mappings

---

## 2. Supported Loaders & Dependencies

| Loader | Minecraft Version | Dependency Versions | Artifact Name | Verification Status |
|---|---|---|---|---|
| **Fabric** | **26.2** | Fabric Loader `>=0.18.4`<br>Fabric API `0.159.0+26.2`<br>ModMenu `18.0.0` | `jumpdelayfix-fabric-26.2-1.3.2.jar` | **VERIFIED (PASS)** |
| **NeoForge** | **26.2** | NeoForge `26.2.0.79`<br>Loader Range `[26.2,26.3)`<br>JavaFML `[4,)` | `jumpdelayfix-neoforge-26.2-1.3.2.jar` | **VERIFIED (PASS)** |

---

## 3. Artifact Details & Checksums

| Artifact | Size | SHA-256 Checksum |
|---|---|---|
| `jumpdelayfix-fabric-26.2-1.3.2.jar` | ~243 KB | `9dd7560887480b62ad1de16e3d1646babe03b7cdf3f3ec5ada82ccf91b960543` |
| `jumpdelayfix-neoforge-26.2-1.3.2.jar` | ~242 KB | `827e7fe61f3e096c671f0397721e02c6d2e56bdb7c7103b1198a67790975857f` |

---

## 4. Verification Commands

```bash
./gradlew spotlessApply validateStructure validateSourceHygiene
./gradlew test --rerun
./gradlew strictCheck publishReadyCheck buildAll
./scripts/probe-minecraft-versions.sh --versions "26.2" --tasks "test publishReadyCheck"
```
