# Changelog

All notable changes to this project will be documented in this file.

## 1.3.2

### Added
- Canonical compatibility matrix (`docs/compatibility-matrix.md`) detailing Minecraft 26.2 support, loader dependencies, artifact packaging, and verification status.
- Comprehensive 9-step procedure for porting and adding new Minecraft versions documented in `docs/development.md`.
- Explicit player state predicates in Fabric and NeoForge input adapters covering active swimming (`isSwimming`), submerged status (`isInWater`), visual crawling (`isVisuallyCrawling`), vehicle passenger immunity (`isPassenger`), and flight/gliding.
- Inconclusive jump tracking diagnostics (`getInconclusiveJumpCount()`) and regression verification.

### Changed
- Refined jump confirmation heuristics in `JumpHandler`: airborne jumps exceeding the timeout window without detected rollback or lift confirmation are marked inconclusive rather than falsely credited as successful confirmations, preventing skewed server latency statistics and inappropriate adaptive penalties.
- Reorganized `ConfigManager` package structure to `com.kizio.jumpdelayfix.ConfigManager` in pure Java `:common` with package structure validation.
- Completely removed dead and unused GameTest structure files from NeoForge resources.
- Eliminated all `@SuppressWarnings` annotations across the codebase and migrated keymapping categories to non-deprecated APIs with automated `validateSourceHygiene` enforcement.
- Strengthened NeoForge loader version ranges to strictly bounded intervals (`[26.2,26.3)`), preventing runtime mismatches against untested future major releases.
- Hardened CI/CD release workflow to ensure each artifact is published exclusively for its exact compatible Minecraft version without broad version leakage.

### Compatibility
- Minecraft: 26.2
- Fabric: Loader 0.18.4+ / Fabric API 0.159.0+26.2
- NeoForge: 26.2.0.79+ (Loader `[26.2,26.3)`)
- Java: 25

## 1.3.1

### Added
- In-game "Reset to Defaults" button in the settings screen for both Fabric and NeoForge, allowing quick restoration of default configuration.
- Enhanced player lifecycle validity checks (sleeping state, alive check, entity instance validation across respawns and dimension changes) to immediately reset transient jump delay tracking.
- Client connection and respawn event listeners on both loaders to cleanly reset transient jump states upon joining worlds or respawning.
- Localized reset button strings (`gui.jumpdelayfix.reset_defaults`) across all 10 supported languages (`en_us`, `pl_pl`, `de_de`, `es_es`, `fr_fr`, `it_it`, `ja_jp`, `pt_br`, `ru_ru`, `zh_cn`).
- Dedicated `docs/installation.md` and `docs/troubleshooting.md` guides along with updated documentation and wiki instructions.
- Unified release script (`scripts/release.sh`) with multi-branch awareness, `--dry-run` and `--publish` modes, and verification gates.

### Changed
- Strengthened concurrency thread safety in `JumpDelayFix` by ensuring synchronized access across runtime state queries (`isEnabled()`, `getProfile()`).
- Corrected Polish localization diacritics in `pl_pl.json`.
- Updated GitHub Actions CI and CodeQL workflows for broader support branch coverage and reliable build performance.

### Compatibility
- Minecraft: 26.2
- Fabric: Loader 0.18.4+ / Fabric API 0.159.0+26.2
- NeoForge: 26.2.0.79+
- Java: 25

## 1.3.0 - 2026-09-06

### Added
- Updated mod support for Minecraft 26.2 for Fabric and NeoForge.

### Compatibility
- Minecraft: 26.2
- Fabric: Loader 0.18.4+ / Fabric API 0.159.0+26.2
- NeoForge: 26.2.0+

## 1.2.0

### Added
- Updated mod support for Minecraft 26.1.x line (26.1, 26.1.1, and 26.1.2) in a single unified JAR for Fabric and NeoForge.

### Changed
- Migrated codebase and toolchain to Java 25.
- Updated Fabric KeyMappings integration using Fabric API `KeyMappingHelper`.
- Updated NeoForge key mappings registration using modern NeoForge client event handlers.

### Compatibility
- Minecraft: 26.1, 26.1.1, 26.1.2
- Fabric: Loader 0.18.4+ / Fabric API 0.155.2+26.1.2
- NeoForge: 26.1.2.103+

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
