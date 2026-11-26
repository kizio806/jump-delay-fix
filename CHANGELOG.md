# Changelog

All notable changes to this project will be documented in this file.

## 2.0.0+mc1.20.2-1.20.4

### Added
- **Unified Minecraft 1.20.2–1.20.4 Support**: Complete, unified support for Minecraft versions 1.20.2, 1.20.3, and 1.20.4 across both Fabric and NeoForge loaders.
- **Universal Single-JAR Architecture**: Exactly one Fabric JAR (`jump-delay-fix-2.0.0-mc1.20.2-1.20.4-fabric.jar`) and one NeoForge JAR (`jump-delay-fix-2.0.0-mc1.20.2-1.20.4-neoforge.jar`) support all 3 Minecraft versions seamlessly via broad compatibility ranges (`fabric_minecraft_version_range=>=1.20.2 <1.20.5`, `minecraft_version_range=[1.20.2,1.20.5)`, `neoforge_loader_range=[20.2,20.5)`).
- **Synchronized Global 2.0.0 Standard**: Shares the unified SemVer 2.0.0 architecture, zero-overhead input polling, pure-Java common module, and robust multiplayer anti-cheat heuristics with `main` and all active support lines.
- **Cross-Version Loader Adaptation**:
  - Keybinding registration using `String` categories (`category.jumpdelayfix`) matching the Minecraft 1.20.x API contract.
  - NeoForge client lifecycle using `TickEvent.ClientTickEvent` (`event.phase == TickEvent.Phase.END`) and `ConfigScreenHandler.ConfigScreenFactory` across NeoForge 20.2 through 20.4.
  - Java 17 toolchain baseline ensuring full compatibility with launchers and environments targeting Minecraft 1.20.2–1.20.4 without requiring Java 21.
  - Fabric player state queries compiled against Yarn 1.20.4 (`isFallFlying`) and validated against Fabric intermediary (`method_6128()Z`) for 100% binary compatibility across 1.20.2, 1.20.3, and 1.20.4 runtimes.

### Technical & Compatibility Boundaries
- **Upper Boundary (1.20.4 vs 1.20.5) Justification**:
  - **Java 17 vs Java 21**: Minecraft 1.20.2–1.20.4 requires Java 17, while 1.20.5 bumped the engine baseline to Java 21.
  - **NeoForge Event Model**: NeoForge 20.2–20.4 uses `net.neoforged.neoforge.event.TickEvent.ClientTickEvent` and `net.neoforged.neoforge.client.ConfigScreenHandler`. NeoForge 20.5/20.6 removed `TickEvent` entirely, replacing it with `net.neoforged.neoforge.client.event.ClientTickEvent.Post` and `IConfigScreenFactory`.
- **Lower Boundary (1.20.2 vs 1.20.1) Justification**:
  - **NeoForge / Forge Split**: Minecraft 1.20.1 NeoForge was a fork of Forge 47 using `net.minecraftforge.*` packages, `META-INF/mods.toml`, and SRG runtime remapping. NeoForge 20.2 completely rebranded to `net.neoforged.*`, introduced `neoforge.mods.toml`, and switched to official Mojang mappings in production.
  - **Vanilla `Screen.renderBackground`**: In 1.20.0–1.20.1, `Screen.renderBackground(DrawContext)` took 1 parameter; 1.20.2 expanded the signature to 4 parameters `(DrawContext, int, int, float)`.
  - Therefore, `1.20.2–1.20.4` forms a distinct, unified binary compatibility boundary separate from `1.20.0–1.20.1` and `1.20.5–1.20.6`.

### Compatibility
- Minecraft: 1.20.2, 1.20.3, 1.20.4 (Both Fabric and NeoForge)
- Fabric: Loader `>=0.18.4`, Fabric API `>=0.91.0`
- NeoForge: Loader `[20.2, 20.5)` (tested on 20.4.253)
- Java: 17

## 2.0.0+mc1.20.5-1.20.6

### Added
- **Unified Minecraft 1.20.5–1.20.6 Support**: Complete, unified support for Minecraft versions 1.20.5 and 1.20.6 across both Fabric and NeoForge loaders.
- **Universal Single-JAR Architecture**: Exactly one Fabric JAR (`jump-delay-fix-2.0.0-mc1.20.5-1.20.6-fabric.jar`) and one NeoForge JAR (`jump-delay-fix-2.0.0-mc1.20.5-1.20.6-neoforge.jar`) support both Minecraft versions seamlessly via broad compatibility ranges (`fabric_minecraft_version_range=>=1.20.5 <1.20.7`, `minecraft_version_range=[1.20.5,1.20.7)`, `neoforge_loader_range=[20.5,20.7)`).
- **Synchronized Global 2.0.0 Standard**: Shares the unified SemVer 2.0.0 architecture, zero-overhead input polling, pure-Java common module, and robust multiplayer anti-cheat heuristics with `main` and all active support lines.
- **Cross-Version Loader Adaptation**:
  - Keybinding registration using `String` categories (`category.jumpdelayfix`) matching the Minecraft 1.20.x API contract.
  - NeoForge client lifecycle using `ClientTickEvent.Post` and environment detection utilizing `FMLEnvironment.dist == Dist.CLIENT` across NeoForge 20.5 through 20.6.
  - Fabric player state queries compiled against Yarn 1.20.6 (`isFallFlying`) and validated against Fabric intermediary (`method_6128()Z`) for 100% binary compatibility across 1.20.5 and 1.20.6 runtimes.

### Technical & Compatibility Boundaries
- **Minecraft 1.20.5/1.20.6 vs 1.20.4 Boundary Justification**:
  - **Java 21 Runtime Baseline**: Minecraft 1.20.5 introduced Java 21 as a mandatory runtime requirement, breaking bytecode backward compatibility with Java 17 runtimes used by Minecraft 1.20.0–1.20.4.
  - **NeoForge Event Model**: NeoForge 20.6 replaced legacy `TickEvent.ClientTickEvent` with `net.neoforged.neoforge.client.event.ClientTickEvent.Post`. `ClientTickEvent.Post` does not exist in NeoForge 20.4, making a unified binary spanning 1.20.4 and 1.20.5 impossible without unsafe reflection or multi-release tricks.
  - **Component Rewrite**: Minecraft 1.20.5 introduced the item component system and internal engine overhauls.
  - Therefore, `1.20.5–1.20.6` forms a distinct, unified binary compatibility boundary separate from `1.20.2–1.20.4`.
- **Yarn Mapping Evolution**: In Yarn mappings for 1.20.6, `method_6128` maps to `isFallFlying`. Fabric Loom remaps this to `net.minecraft.class_1309.method_6128()Z`, ensuring identical bytecode execution across 1.20.5 and 1.20.6.

### Compatibility
- Minecraft: 1.20.5, 1.20.6 (Both Fabric and NeoForge)
- Fabric: Loader `>=0.18.4`, Fabric API `>=0.97.7`
- NeoForge: Loader `[20.5, 20.7)` (tested on 20.6.144)
- Java: 21

## 2.0.0+mc1.21.0-1.21.8

### Added
- **Unified Minecraft 1.21.0–1.21.8 Support**: Complete, unified support for Minecraft versions 1.21.0, 1.21.1, 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, and 1.21.8 across both Fabric and NeoForge loaders.
- **Universal Single-JAR Architecture**: Exactly one Fabric JAR (`jump-delay-fix-2.0.0-mc1.21.0-1.21.8-fabric.jar`) and one NeoForge JAR (`jump-delay-fix-2.0.0-mc1.21.0-1.21.8-neoforge.jar`) support all 9 Minecraft versions seamlessly via broad compatibility ranges (`fabric_minecraft_version_range=>=1.21.0 <1.21.9`, `minecraft_version_range=[1.21.0,1.21.9)`, `neoforge_loader_range=[21.0,21.9)`).
- **Synchronized Global 2.0.0 Standard**: Shares the unified SemVer 2.0.0 architecture, zero-overhead input polling, pure-Java common module, and robust multiplayer anti-cheat heuristics with `main` and all active support lines.
- **Cross-Version Loader Adaptation**:
  - Keybinding registration using `String` categories (`category.jumpdelayfix`) matching the pre-1.21.9 Mojang API contract.
  - NeoForge environment detection utilizing `FMLEnvironment.dist == Dist.CLIENT` across NeoForge 21.0 through 21.8.
  - Fabric player state queries compiled against Yarn 1.21.8 (`isGliding`) and validated against Fabric intermediary (`method_6128()Z`) for 100% binary compatibility across 1.21.0 through 1.21.8 runtimes.

### Technical & Compatibility Boundaries
- **Minecraft 1.21.8 vs 1.21.9 Boundary Justification**:
  - In Minecraft 1.21.9 ("The Copper Age"), Mojang introduced `KeyBinding.Category` (Fabric) and `KeyMapping.Category` (NeoForge), replacing `String category` parameters in keymapping constructors. JARs compiled against 1.21.9+ crash on 1.21.8 and earlier with `NoSuchMethodError` / `NoSuchFieldError`.
  - In NeoForge 21.9+, `FMLEnvironment.getDist()` was introduced and replaced public direct access to `FMLEnvironment.dist`. JARs referencing `getDist()` fail on NeoForge 21.0–21.8.
  - Therefore, `1.21.0–1.21.8` forms a distinct, unified binary compatibility boundary separate from `1.21.9–1.21.11`.
- **Yarn Mapping Evolution**: In Yarn mappings, `method_6128` was named `isFallFlying` in 1.21.0–1.21.1 and renamed to `isGliding` in 1.21.2+. Because Fabric remapping targets the immutable intermediary method descriptor `method_6128()Z`, compiling with Yarn 1.21.8 produces bytecode that executes identically across all 1.21.0–1.21.8 clients.

### Compatibility
- Minecraft: 1.21.0, 1.21.1, 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8 (Both Fabric and NeoForge)
- Fabric: Loader `>=0.18.4`, Fabric API `>=0.100.8`
- NeoForge: Loader `[21.0, 21.9)` (tested on 21.0.167, 21.1.267, 21.3.100, 21.4.161, 21.5.101, 21.7.25-beta, 21.8.57)
- Java: 21

## [Unreleased]

### Changed
- **Unified Global Versioning Standard**: Migrated versioning architecture to synchronized Semantic Versioning with Minecraft compatibility build metadata (`2.0.0+mc1.21.9-1.21.11`).
- **Synchronized Support Branch Parity**: All active compatibility lines (`main`, `support/26.2.x`, `support/26.1.x`, `support/1.21.9-1.21.11`) now share the global base version `2.0.0`.
- **Deterministic Release Artifact Naming**: Standardized multi-loader release JAR outputs to `jump-delay-fix-<BASE_VERSION>-<MC_METADATA>-fabric.jar` and `jump-delay-fix-<BASE_VERSION>-<MC_METADATA>-neoforge.jar`.
- **Zero Loader Duplication**: Extracted shared client classes (`StatusMessages`, `ClientJumpInput`, `SettingsScreen`) to `common/src/client/java`, compiling the shared client sources directly into Fabric and NeoForge loader artifacts without redundant duplication.
- **Quality Gates & Metrics Transparency**: Removed `sonar.cpd.exclusions` from build configuration and SonarCloud analysis, ensuring honest duplication tracking across loaders. Applied JaCoCo test reporting across all subprojects.
- **CI/CD Security Hardening**: Pinned all GitHub Actions across all workflow definitions (`ci.yml`, `codeql.yml`, `release.yml`, `sonarcloud.yml`, `wiki-sync.yml`, `labeler.yml`) to immutable full commit SHAs.

## 1.1.2

### Added
- Unified Minecraft 1.21.9, 1.21.10, and 1.21.11 support across both Fabric and NeoForge loaders.
- Unified single-JAR architecture per loader: Jump Delay Fix Fabric JAR and NeoForge JAR seamlessly support Minecraft 1.21.9, 1.21.10, and 1.21.11 via comprehensive version ranges (`fabric_minecraft_version_range=>=1.21.9 <1.21.12`, `minecraft_version_range=[1.21.9,1.21.12)`).
- Canonical compatibility matrix (`docs/compatibility-matrix.md`) detailing Minecraft 1.21.x versions, loader dependencies, and release targets.
- Comprehensive 9-step procedure for porting and adding new Minecraft versions documented in `docs/development.md`.
- Explicit player state predicates in Fabric (Yarn mappings: `isTouchingWater`, `isSwimming`, `isCrawling`, `hasVehicle`, `isGliding`) and NeoForge (`isInWater`, `isSwimming`, `isVisuallyCrawling`, `isPassenger`, `isFallFlying`) input adapters.
- Inconclusive jump tracking diagnostics (`getInconclusiveJumpCount()`) and regression verification.

### Changed
- Zero-allocation `JumpInput` architecture: refactored input abstraction from per-tick functional allocations to direct state query methods, eliminating per-tick garbage collection pressure during the client tick loop.
- Memory leak prevention: bounded `ServerAdaptiveStats` history with a 256-entry LRU (Least Recently Used) cache, preventing unbounded heap growth across long-running multiplayer sessions.
- Concurrency hardening: added synchronization guards to profile queries and server adaptive tracking against race conditions during asynchronous network/tick events.
- Refined jump confirmation heuristics in `JumpHandler`: airborne jumps exceeding the timeout window without detected rollback or lift confirmation are marked inconclusive rather than falsely credited as successful confirmations, preventing skewed server latency statistics and inappropriate adaptive penalties.
- Reorganized `ConfigManager` package structure to `com.kizio.jumpdelayfix.ConfigManager` in pure Java `:common` with package structure validation.
- Completely removed dead and unused GameTest structure files and duplicate resources.
- Eliminated all `@SuppressWarnings` annotations across the codebase with automated `validateSourceHygiene` enforcement.
- Strengthened NeoForge loader version ranges to strictly bounded intervals (`[21.9,21.10)`, `[21.10,21.11)`, `[21.11,21.12)`).
- Hardened CI/CD release workflow to ensure each artifact is published exclusively for its exact compatible Minecraft version without broad version leakage.

### Fixed
- Localized settings menu "Done" button (`gui.jumpdelayfix.option.done`) across Italian (`Fatto`), Japanese (`完了`), Portuguese (`Concluído`), Russian (`Готово`), and Simplified Chinese (`完成`).
- Rectified profile cycling order in documentation and wiki guides to reflect actual implementation sequence (`SMART` → `STABLE` → `COMPETITIVE`).
- Fixed misleading keybinding descriptions in troubleshooting documentation across all loaders (`J` for toggle, `H` for profile cycle, `O` for settings GUI).
- Remedied broken anchor links and external repository references across project documentation, ensuring clean onboarding for new contributors.

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
