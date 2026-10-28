# Changelog

All notable changes to this project will be documented in this file.

## 1.1.2

### Added
- Unified Minecraft 1.21.9, 1.21.10, and 1.21.11 support across both Fabric and NeoForge loaders.
- Dedicated multi-version build script (`scripts/build-all-versions.sh`) generating 6 versioned production JARs with checksum verification.
- Canonical compatibility matrix (`docs/compatibility-matrix.md`) detailing Minecraft 1.21.x versions, loader dependencies, and release targets.
- Comprehensive 9-step procedure for porting and adding new Minecraft versions documented in `docs/development.md`.
- Explicit player state predicates in Fabric (Yarn mappings: `isTouchingWater`, `isSwimming`, `isCrawling`, `hasVehicle`, `isGliding`) and NeoForge (`isInWater`, `isSwimming`, `isVisuallyCrawling`, `isPassenger`, `isFallFlying`) input adapters.
- Inconclusive jump tracking diagnostics (`getInconclusiveJumpCount()`) and regression verification.

### Changed
- Refined jump confirmation heuristics in `JumpHandler`: airborne jumps exceeding the timeout window without detected rollback or lift confirmation are marked inconclusive rather than falsely credited as successful confirmations, preventing skewed server latency statistics and inappropriate adaptive penalties.
- Reorganized `ConfigManager` package structure to `com.kizio.jumpdelayfix.ConfigManager` in pure Java `:common` with package structure validation.
- Completely removed dead and unused GameTest structure files and duplicate resources.
- Eliminated all `@SuppressWarnings` annotations across the codebase with automated `validateSourceHygiene` enforcement.
- Strengthened NeoForge loader version ranges to strictly bounded intervals (`[21.9,21.10)`, `[21.10,21.11)`, `[21.11,21.12)`).
- Hardened CI/CD release workflow to ensure each artifact is published exclusively for its exact compatible Minecraft version without broad version leakage.

### Compatibility
- Minecraft: 1.21.9, 1.21.10, 1.21.11 (Both Fabric and NeoForge)
- Fabric: Loader 0.18.4+ / Fabric API 0.134.1+ (1.21.9), 0.138.4+ (1.21.10), 0.141.6+ (1.21.11)
- NeoForge: 21.9.16+ (1.21.9), 21.10.64+ (1.21.10), 21.11.48+ (1.21.11)
- Java: 21

## 1.1.1

### Added
- In-game "Reset to Defaults" option in the settings GUI for quickly restoring baseline configuration.
- Diagnostic inspection queries on `JumpDelayFix` for active server, confirmed jumps, and rejected jumps.
- Comprehensive installation and troubleshooting guides with multiplayer anti-cheat notices.

### Fixed
- Fixed thread-safety synchronization on `JumpDelayFix.getProfile()` to align getter and setter concurrency.
- Added player state validation (`isAlive()`, `!isSleeping()`, entity ID transition check) to prevent unwanted jump attempts during respawn, death, and bed usage.
- Corrected Polish localization diacritics in `pl_pl.json`.

### Technical
- Upgraded NeoForge toolchain to `21.11.48` baseline with cross-version compatibility (`KeyMapping.Category.MISC`, `FMLEnvironment.getDist()`) tested across NeoForge `21.9.16-beta` (1.21.9), `21.10.64` (1.21.10), and `21.11.48` (1.21.11).
- Updated GitHub Actions CI/CD workflows to include `support/**` branch coverage and disable build cache in CodeQL analysis.
- Cleaned and extended unit and integration test coverage.

### Compatibility
- Minecraft: Fabric: 1.21.11; NeoForge: 1.21.9, 1.21.10, 1.21.11
- Fabric: Loader 0.18.4+ / Fabric API 0.141.6+1.21.11
- NeoForge: 21.9.16+ (loader range `[21.9,)`, game version range `[1.21, 1.22)`)
- Java: 21

## 1.1.0

### Added
- Smart Auto-Profile switching: The mod now dynamically adapts to server latency and rollback rates, choosing the best jump profile automatically.
- Server tracking: The optimal profile for each server is now remembered and automatically applied upon joining.

### Changed
- Improved Competitive Profile to more strictly enforce latency checks.
- Unified Fabric and NeoForge client integration code for a cleaner architecture.
- Streamlined configuration storage and serialization for faster loading and saving.

### Removed
- Removed unused block and item registry abstractions to drastically reduce code bloat.

### Technical
- Flattened the package structure to eliminate unnecessary deep nesting (e.g., `com.kizio.jumpdelayfix.common` -> `com.kizio.jumpdelayfix`).
- Renamed internal state and management classes to more accurately reflect their runtime responsibilities.
- Optimized event handling by consolidating the listener structure into unified entry points for Fabric and NeoForge.

### Compatibility
- Minecraft: Fabric: 1.21.11; NeoForge: 1.21.9, 1.21.10, 1.21.11
- Fabric: Loader 0.18.4+ / Fabric API 0.141.6+1.21.11
- NeoForge: 21.1.174+
- Java: 21
