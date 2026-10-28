# Code Style

## Architectural Baseline

Jump Delay Fix is engineered as a zero-overhead, multi-loader Minecraft mod adhering to clean architecture principles:

- loader-independent business logic and configuration live entirely in `:common`
- platform-specific modules (`:fabric` and `:neoforge`) are thin adapters bridging client events to `:common`
- release automation and documentation maintain identical standards across all active branches

## Package Naming

- root package: `com.kizio.jumpdelayfix`
- maintainer identifier: `kizio`
- do not encode temporary account suffixes such as `kizio806` into code namespaces

## Module Boundaries

- `common` owns loader-agnostic behavior, config, and tests
- `fabric` and `neoforge` should stay thin and adapter-oriented
- do not duplicate shared behavior across loader modules unless loader APIs force a real divergence

## Naming Rules

- use direct names over clever names
- booleans should read as predicates such as `isEnabled`, `hasRollbackWindow`, `shouldAutoSwitch`
- keep `mod_id`, package names, and generated metadata aligned

## File And Repo Naming

- repository docs should expose consistent documentation across projects:
  `CHANGELOG.md`, `CONTRIBUTING.md`, `SECURITY.md`, `docs/architecture.md`, `docs/development.md`, `docs/installation.md`, `docs/modrinth.md`, `docs/releases.md`, `docs/troubleshooting.md`, `docs/wiki/*`
- docs should describe what the repository actually targets
- release automation should publish the same verified artifacts described by the README and docs

## Hard Quality Gates

- `strictCheck` is mandatory for CI and release workflows
- Spotless formatting is enforced via `spotlessCheck` (and auto-formatted via `spotlessApply`)
- Java sources in `common/src`, `fabric/src`, and `neoforge/src` allow meaningful explanatory comments, while trivial or redundant comments are forbidden
- Java sources must not contain `TODO`, `FIXME`, or `XXX` markers (enforced by `validateSourceHygiene`)
- root `src/` directory is prohibited; all sources must reside in `common/`, `fabric/`, or `neoforge/` (enforced by `validateStructure`)
- wildcard imports are forbidden (enforced by `validateSourceHygiene`)
- `@SuppressWarnings` annotations are strictly forbidden across all modules; warnings must be resolved by fixing root causes rather than silencing them (enforced by `validateSourceHygiene`)
- compilation is enforced with `-Xlint:all -Werror`

## Procedure for Adding or Porting to a New Minecraft Version

When adding support for a new Minecraft version (minor update or new major line), follow this 9-step procedure:

### 1. Select Compile Target and Support Range
- Identify the base Minecraft version (e.g. `26.4` or `26.3.1`).
- Decide whether the new version can share an existing minor line (single patch-line rule: `fabric_game_versions` and `neoforge_game_versions` must share the same major.minor) or requires branching into a new support line.

### 2. Determine Loader & Dependency Versions
- Consult Fabric Maven and NeoForge Maven for matching runtime artifacts:
  - Fabric Loader (e.g. `>=0.18.4`)
  - Fabric API (e.g. `0.160.6+26.3`)
  - NeoForge (e.g. `26.3.0.1-beta`)
  - ModMenu (e.g. `18.0.0` or `17.0.0`)
- Determine the NeoForge loader range (e.g. `[26.3,26.4)`). Ensure the range is bounded on both sides to prevent running against untested future major versions.

### 3. Check Mappings & Player Input APIs
- For modern lines (Minecraft >= 26.x), official Mojang mappings are standard across both Fabric and NeoForge.
- For 1.21.x legacy lines, Fabric uses Yarn (`net.fabricmc.yarn`).
- Inspect client player classes for changes to:
  - Jump execution: `LocalPlayer.jumpFromGround()` (Mojang) vs `ClientPlayerEntity.jump()` (Yarn)
  - Water/swimming: `isInWater()`, `isSwimming()` vs `isTouchingWater()`, `isSwimming()`
  - Crawling: `isVisuallyCrawling()` vs `isCrawling()`
  - Gliding / Fall flying: `isFallFlying()` vs `isGliding()`
  - Vehicles / Passengers: `isPassenger()` vs `hasVehicle()`

### 4. Update Version Properties
Use the automated updater script rather than manual editing:
```bash
./scripts/set-minecraft-version.sh \
  --base-minecraft <mc_version> \
  --supported-versions "<supported_versions>" \
  --fabric-api "<fabric_api_version>" \
  --neoforge "<neoforge_version>" \
  --mod-version <mod_version>
```
Verify `gradle.properties` matches the matrix intersection rule (`modrinth_game_versions = fabric_game_versions ∩ neoforge_game_versions`).

### 5. Probe Candidate Versions
Run the isolated candidate probe to verify compilation and dependency resolution before committing changes:
```bash
./scripts/probe-minecraft-versions.sh \
  --versions "<comma_separated_versions>" \
  --tasks "test publishReadyCheck"
```

### 6. Build Release Artifacts
- For single-line branches:
  ```bash
  ./gradlew clean buildAll
  ```
- For multi-artifact lines (e.g. 1.21.9–1.21.11):
  ```bash
  ./scripts/build-all-versions.sh
  ```

### 7. Inspect Built JARs and Checksums
Directly inspect the ZIP archive contents to guarantee clean production packaging:
```bash
# Check fabric.mod.json depends and absence of test structures
unzip -p fabric/build/libs/*.jar fabric.mod.json | grep -A 5 '"depends"'
unzip -l fabric/build/libs/*.jar | grep -E "test|Test|\.nbt|\.snbt"

# Check neoforge.mods.toml dependencies
unzip -p neoforge/build/libs/*.jar META-INF/neoforge.mods.toml | grep -A 10 '\[\[dependencies'

# Compute SHA-256 checksums
sha256sum fabric/build/libs/*.jar neoforge/build/libs/*.jar
```

### 8. Execute Quality & Release Readiness Gates
Run the entire validation gauntlet:
```bash
# Code formatting
./gradlew spotlessApply

# Structural layout and source hygiene
./gradlew validateStructure validateSourceHygiene

# Unit and integration test suite
./gradlew test --rerun

# Matrix consistency, SemVer, and dedicated server safety
./gradlew strictCheck publishReadyCheck buildAll
```

### 9. Update Compatibility Matrix and Release Documentation
- Update `docs/compatibility-matrix.md` with the newly verified versions, artifact names, and SHA-256 hashes.
- Update `README.md` and `docs/wiki/Installation-and-Compatibility.md`.
- Update `.github/workflows/release.yml` if new versioned publication steps are needed.
- Document any user-visible changes in `CHANGELOG.md`.

## Documentation Style

- short sections
- no filler
- explain why a structure exists, not only what files exist
