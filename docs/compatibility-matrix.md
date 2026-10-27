# Jump Delay Fix — Compatibility Matrix (support/1.21.9-1.21.11)

This document describes the compatibility and release specifications for the **`support/1.21.9-1.21.11`** branch of Jump Delay Fix.

> 🔗 For the canonical global compatibility matrix across all branches, see [compatibility-matrix.md on main](https://github.com/kizio806/jump-delay-fix/blob/main/docs/compatibility-matrix.md).

---

## 1. Branch Specifications

- **Branch:** `support/1.21.9-1.21.11`
- **Mod Version:** `1.1.2`
- **Target Minecraft Versions:** `1.21.9`, `1.21.10`, `1.21.11`
- **Default Compile Target:** `1.21.11`
- **Java Toolchain:** **Java 21**
- **Mappings:**
  - Fabric: Yarn mappings (`1.21.x+build.1`)
  - NeoForge: Official Mojang mappings

---

## 2. Multi-Artifact Strategy (6 Dedicated JARs)

Because Fabric API publishes patch-locked metadata (`0.134.1+1.21.9` binds to `1.21.9`, `0.138.4+1.21.10` to `1.21.10`, `0.141.6+1.21.11` to `1.21.11`), a single JAR cannot satisfy all three versions simultaneously without dependency conflicts.

Consequently, `support/1.21.9-1.21.11` produces **6 dedicated release JARs** built via `./scripts/build-all-versions.sh`, each strictly declaring its target game version:

| Loader | Minecraft Version | Dependency Versions | Production Artifact | SHA-256 Checksum | Verification Status |
|---|---|---|---|---|---|
| **Fabric** | **1.21.9** | Fabric API `0.134.1+1.21.9`<br>Yarn `1.21.9+build.1`<br>ModMenu `16.0.1` | `jumpdelayfix-fabric-1.21.9-1.1.2.jar` | `5d7c16efa60928930d399239c881b5bcf238c7c17fe4a16f8b601dd87fea337e` | **VERIFIED (Probe PASS)** |
| **Fabric** | **1.21.10** | Fabric API `0.138.4+1.21.10`<br>Yarn `1.21.10+build.1`<br>ModMenu `16.0.1` | `jumpdelayfix-fabric-1.21.10-1.1.2.jar` | `e5e54bad2de408b881cd9d1232b45e03919d990231715ac5fa3d557ecda937d3` | **VERIFIED (Probe PASS)** |
| **Fabric** | **1.21.11** | Fabric API `0.141.6+1.21.11`<br>Yarn `1.21.11+build.1`<br>ModMenu `17.0.0-beta.2` | `jumpdelayfix-fabric-1.21.11-1.1.2.jar` | `d08a8dcb0fe1f548cb9e7ce47b131c13be406acf75f3abf8f674fc02fe5d194a` | **VERIFIED (Probe PASS)** |
| **NeoForge** | **1.21.9** | NeoForge `21.9.16-beta`<br>Loader Range `[21.9,21.10)` | `jumpdelayfix-neoforge-1.21.9-1.1.2.jar` | `159f961ac5e2409cea1da240376bb30cb8184d24b17ea855d36565809f82b8a4` | **VERIFIED (Probe PASS)** |
| **NeoForge** | **1.21.10** | NeoForge `21.10.64`<br>Loader Range `[21.10,21.11)` | `jumpdelayfix-neoforge-1.21.10-1.1.2.jar` | `11c52f9389174be361a0062ede39b870eb7443b89cc476c1efc2b073c71a688d` | **VERIFIED (Probe PASS)** |
| **NeoForge** | **1.21.11** | NeoForge `21.11.48`<br>Loader Range `[21.11,21.12)` | `jumpdelayfix-neoforge-1.21.11-1.1.2.jar` | `2a3374c34c69616f766e8024bef172b88cd1c4f3737eed8c96ec7bbd5a0c73bb` | **VERIFIED (Probe PASS)** |

---

## 3. Publication Architecture on Modrinth & CurseForge

To avoid duplicate version numbers and prevent attaching JARs to incompatible Minecraft versions:
- Releases are published as **3 distinct version entries** (`1.1.2-1.21.9`, `1.1.2-1.21.10`, `1.1.2-1.21.11`).
- Each entry groups both the Fabric and NeoForge JARs for that specific Minecraft version.
- Neither file is ever published with the global version list.

---

## 4. Verification & Build Commands

```bash
# 1. Run unit and contract tests
./gradlew test --rerun

# 2. Run version scripts unit tests (including sandbox hermeticity)
./scripts/test-version-scripts.sh

# 3. Build and verify all 6 release artifacts into release-artifacts/
./scripts/build-all-versions.sh

# 4. Probe across all three target versions
./scripts/probe-minecraft-versions.sh --versions "1.21.9,1.21.10,1.21.11" --tasks "test publishReadyCheck"
```
