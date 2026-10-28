# Release Management & Publishing Standards

This document defines the release lifecycle, multi-loader packaging invariants, platform publishing standards, and incident prevention rules for the **Jump Delay Fix** project.

---

## 1. Golden Packaging Invariant (Universal Rule)

Every active release line produces **EXACTLY TWO production JARs** built via `./gradlew buildAll`:
1. **EXACTLY ONE Fabric JAR** (e.g. `jumpdelayfix-fabric-<base_mc>-<version>.jar`)
2. **EXACTLY ONE NeoForge JAR** (e.g. `jumpdelayfix-neoforge-<base_mc>-<version>.jar`)

### Cross-Version Range Rules
For release lines supporting multiple Minecraft patch versions (e.g., `1.21.9 – 1.21.11` on `support/1.21.9-1.21.11` or `26.1 – 26.1.2` on `support/26.1.x`):
- **Single Unified JAR Per Loader:** A single JAR covers the entire patch line. Never build or distribute multiple separate single-version JARs per patch.
- **Internal Range Alignment (`fabric.mod.json`):**
  - `minecraft`: declared with `fabric_minecraft_version_range` covering the full range (e.g. `>=1.21.9 <1.21.12` or `>=26.1 <26.2`).
  - `fabric-api`: declared minimum version must match the lowest supported game version in the range (e.g. `>=0.134.1` for 1.21.9 baseline).
- **Internal Range Alignment (`neoforge.mods.toml`):**
  - `minecraft`: declared with `minecraft_version_range` covering the full range (e.g. `[1.21.9,1.21.12)` or `[26.1,26.2)`).
  - `neoforge`: declared with `neoforge_loader_range` covering all corresponding loader releases (e.g. `[21.9,21.12)`).

---

## 2. Platform Publishing Invariants (Modrinth & CurseForge)

Every release must publish **EXACTLY TWO separate entries** on Modrinth and CurseForge:
1. **`Jump Delay Fix <version> for Fabric`**
   - Version ID / Number: `<version>-fabric` (e.g. `1.1.2-fabric`, `1.4.2-fabric`)
   - Loaders: `fabric` ONLY
   - Game Versions: all versions supported by the release line (e.g. `1.21.9`, `1.21.10`, `1.21.11`)
   - File: Single Fabric JAR
2. **`Jump Delay Fix <version> for NeoForge`**
   - Version ID / Number: `<version>-neoforge` (e.g. `1.1.2-neoforge`, `1.4.2-neoforge`)
   - Loaders: `neoforge` ONLY
   - Game Versions: all versions supported by the release line (e.g. `1.21.9`, `1.21.10`, `1.21.11`)
   - File: Single NeoForge JAR

### ⚠️ Prohibited Publishing Anti-Patterns
- **NEVER combine Fabric and NeoForge into a single Modrinth or CurseForge release entry.** Mod launchers (Prism Launcher, Modrinth App, CurseForge App) fail or pick the wrong loader JAR when multiple loaders are bundled into a single release entry.
- **NEVER create multiple release entries per patch version** (e.g. `1.1.2-1.21.9`, `1.1.2-1.21.10`, `1.1.2-1.21.11`). It causes launcher confusion, clutters user updates, and fragments download statistics.
- **NEVER declare external platform game versions that are wider than internal metadata constraints.** If Modrinth lists `1.21.9, 1.21.10, 1.21.11`, the JAR's `fabric.mod.json` MUST accept `>=1.21.9`.

---

## 3. Incident Post-Mortem & Case Study: Modrinth 1.1.1 Metadata Mismatch

### What Happened in 1.1.1?
When version `1.1.1` was tagged and released:
1. In `gradle.properties`, `fabric_minecraft_version_range` was set to `>=1.21.11 <1.21.12` and `fabric_api_version` was `0.141.6+1.21.11`.
2. This happened because a faulty Gradle verification task (`verifyRuntimeDependencyCompatibility`) inspected the build-time Maven artifact of Fabric API (`fabric-api:0.141.6+1.21.11`) and failed if its internal metadata did not declare older Minecraft versions. To pass `./gradlew publishReadyCheck`, `fabric_minecraft_version_range` was incorrectly restricted to 1.21.11 only.
3. However, on Modrinth, the version entry `1.1.1-fabric` was created with `game_versions: ['1.21.9', '1.21.10', '1.21.11']`.
4. **User Impact:** Players on Minecraft 1.21.9 and 1.21.10 downloaded `jumpdelayfix-fabric-1.21.11-1.1.1.jar`. Upon launching Minecraft, Fabric Loader halted startup with an error:
   `Incompatible mod set: jumpdelayfix requires minecraft >=1.21.11 <1.21.12, but only 1.21.9 is installed.`
5. **How It Was Remedied:**
   - On Modrinth web UI: in `1.1.1-fabric`, uncheck `1.21.9` and `1.21.10` so players on older patches are not served broken 1.1.1.
   - For `1.1.2+`: `fabric_minecraft_version_range` was restored to `>=1.21.9 <1.21.12`, `fabric_api_min_version=0.134.1` was added to `fabric.mod.json`, and `verifyRuntimeDependencyCompatibility` was corrected to validate against the compile target `minecraft_version`.

---

## 4. Release Preparation & Execution

### 1. Probe Candidate Versions
Run compatibility probe before changing published metadata to determine which versions are safe to target.

```bash
./scripts/probe-minecraft-versions.sh \
  --versions "1.21.9,1.21.10,1.21.11"
```

### 2. Update Version Metadata
Use the helper script to set the new version details in `gradle.properties`:

```bash
./scripts/set-minecraft-version.sh \
  --base-minecraft <minecraft_version> \
  --supported-versions "<supported_versions>" \
  --mod-version <mod_version>
```

### 3. Verify
Ensure the project builds cleanly and satisfies all gates:

```bash
./gradlew --no-daemon --stacktrace clean buildAll strictCheck publishReadyCheck
```

### 4. Prepare Changelog
Update `CHANGELOG.md` with user-visible changes for the new `<mod_version>`.
That section becomes the release body for both GitHub Releases, Modrinth, and CurseForge.
For the complete list of verified combinations and artifact checksums, refer to [compatibility-matrix.md](compatibility-matrix.md).

### 5. One-Command Release Procedure
To perform the release safely, use the included release script:

```bash
# 1. First run dry-run verification
./scripts/release.sh --dry-run

# 2. When ready, publish the release with one command:
./scripts/release.sh --publish
```

The script automatically:
1. Validates source hygiene, structure, and semver compliance.
2. Executes strict compilation and pre-publish verification gates.
3. Builds release artifacts for all supported loaders (1 Fabric JAR + 1 NeoForge JAR).
4. Verifies artifact existence and file sizes.
5. Creates the release commit (`chore(release): v<mod_version>`) and annotated tag (`v<mod_version>`).
6. Pushes the branch and tag to GitHub.

---

## 5. Automated Delivery Workflow

Pushing the release tag triggers `.github/workflows/release.yml`, which handles the entire deployment process automatically:

- Clean environment build and verification (`./gradlew clean buildAll strictCheck`)
- Release notes generation from `CHANGELOG.md`
- SHA-256 checksum generation for both production JARs
- GitHub Release publishing with Fabric and NeoForge JARs
- Modrinth publishing:
  - Step 1: `Jump Delay Fix <version> for Fabric` (`<version>-fabric`, loader: `fabric`)
  - Step 2: `Jump Delay Fix <version> for NeoForge` (`<version>-neoforge`, loader: `neoforge`)
- CurseForge publishing:
  - Step 1: Fabric release (`loaders: fabric`)
  - Step 2: NeoForge release (`loaders: neoforge`)
