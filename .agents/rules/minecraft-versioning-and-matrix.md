# Minecraft Versioning and Compatibility Matrix

## Purpose
Jump Delay Fix delivers release JARs that target specific Minecraft patch lines while maintaining SemVer compliance. The build system dynamically cross-checks all version properties to ensure that Modrinth, Fabric, and NeoForge metadata agree and do not span across incompatible major/minor versions.

## Standard
- **Single Patch-Line Rule:** `fabric_game_versions` and `neoforge_game_versions` must stay strictly within one patch line (`<major>.<minor>.x`). They must never cross minor release boundaries (e.g. `26.3` and `26.4` cannot be combined in one jar).
- **Matrix Intersection Rule:** `modrinth_game_versions` must equal the exact intersection of `fabric_game_versions` and `neoforge_game_versions`.
- **Range Syntax Compliance:**
  - `fabric_minecraft_version_range` must use format `>=X.Y.Z <X.Y+1`.
  - `minecraft_version_range` (NeoForge) must use format `[X.Y,X.Y+1)`.
- **SemVer Compliance:** `mod_version` must be valid SemVer (`MAJOR.MINOR.PATCH`, e.g. `1.4.1`).
- **One-Command Helper Updates:** Always update version properties using `scripts/set-minecraft-version.sh` rather than manual text editing, preventing syntax and matrix discrepancies.

## Do:
```bash
# Correct: Use the automated version update script
./scripts/set-minecraft-version.sh \
  --base-minecraft 26.3 \
  --fabric-supported-versions "26.3" \
  --neoforge-supported-versions "26.3" \
  --mod-version 1.4.2
```

## Don't:
```properties
# Bad: Manually editing gradle.properties with mismatched ranges or cross-minor versions
minecraft_version=26.3
fabric_game_versions=26.3,26.4  # ERROR: Cross-minor versioning not permitted!
minecraft_version_range=[26.0,27.0) # ERROR: Must be [26.3,26.4)
```

## Verification:
An AI agent must run the verification tasks to check compliance:
```bash
./gradlew verifyMinecraftVersionMatrix verifySemverVersion
```
If metadata is invalid, Gradle will throw an explicit exception describing the exact mismatch.
