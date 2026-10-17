# Changelog

All notable changes to this project will be documented in this file.

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
- Updated GitHub Actions CI/CD workflows to include `support/**` branch coverage and disable build cache in CodeQL analysis.
- Cleaned and extended unit and integration test coverage.

### Compatibility
- Minecraft: 1.21.9, 1.21.10, 1.21.11
- Fabric: Loader 0.18.4+
- NeoForge: 21.1.174+

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
- Minecraft: 1.21.1
- Fabric: Loader 0.18.4+
- NeoForge: 21.1.174+
