---
name: port-minecraft-version
description: Guide and execute the end-to-end upgrade or port of the mod to a new Minecraft version, updating version matrices, toolchains, mappings, and ranges.
---

# Port Minecraft Version Skill

## When to use it
Use this skill when:
- Porting Jump Delay Fix to a newly released Minecraft snapshot or major/minor version (e.g. 26.4 or 1.21.11).
- Updating Yarn mappings, Fabric API, Loom, or NeoForge ModDev toolchains.
- Testing whether the current codebase compiles against upcoming Minecraft releases.

## Required steps
1. **Probe Candidate Versions:**
   - Run the probing script against candidate versions in an isolated sandbox:
     ```bash
     ./scripts/probe-minecraft-versions.sh --versions "<candidate_version>"
     ```
   - Check if Yarn mappings and dependencies exist for that target version.
2. **Determine Compatibility & API Changes:**
   - Inspect upstream Minecraft decompiled changes affecting `player.onGround()`, `player.jumpFromGround()`, `KeyMapping`, or `Screen`.
3. **Update Version Metadata:**
   - Run the version configuration script:
     ```bash
     ./scripts/set-minecraft-version.sh \
       --base-minecraft <target_mc_version> \
       --fabric-supported-versions "<supported_csv>" \
       --neoforge-supported-versions "<supported_csv>" \
       --mod-version <new_mod_version> \
       --yarn <yarn_mapping_version> \
       --fabric-api <fabric_api_version> \
       --neoforge <neoforge_version>
     ```
4. **Compile and Fix Breakages:**
   - Run `./gradlew clean compileJava compileTestJava` for all subprojects.
   - Adjust loader client adapters (`FabricJumpInput`, `NeoForgeJumpInput`, `FabricSettingsScreen`, `NeoForgeSettingsScreen`) if Minecraft UI or input classes changed.
5. **Verify Version Matrix Gates:**
   - Run `./gradlew verifyMinecraftVersionMatrix verifySemverVersion`.
6. **Execute Full Test & Packaging Gauntlet:**
   - Run `./gradlew strictCheck publishReadyCheck buildAll`.

## Analysis process
```text
1. Sandbox Probing
   ./scripts/probe-minecraft-versions.sh -> Inspect probe logs
2. Property Resolution
   Determine base version, patch line, dependency versions (Fabric API, NeoForge)
3. Metadata Alignment
   Verify fabric_game_versions, neoforge_game_versions, and modrinth_game_versions agree
4. Code Adaptation
   Fix compiler breakages in loader modules (preserving common purity)
5. Validation
   Full buildAll, tests, and verification checks
```

## Expected output
- Fully updated `gradle.properties` compliant with single patch-line constraints.
- Cleanly compiled loader modules targeting the new Minecraft version.
- Verified release artifacts (`fabric/build/libs/*.jar`, `neoforge/build/libs/*.jar`).

## Validation checklist
- [ ] Base version is included in both `fabric_game_versions` and `neoforge_game_versions`.
- [ ] All versions in `fabric_game_versions` stay within the same patch line (`X.Y.x`).
- [ ] `modrinth_game_versions` matches the exact intersection of both loader lists.
- [ ] `fabric_minecraft_version_range` and `minecraft_version_range` ranges match expected bounds.
- [ ] `./gradlew verifyMinecraftVersionMatrix` passes.
- [ ] `./gradlew strictCheck publishReadyCheck` passes.
- [ ] Both Fabric and NeoForge JARs build successfully under `./gradlew buildAll`.
