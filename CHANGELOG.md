# Changelog

All notable changes to this project will be documented in this file.

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
- Minecraft: 1.21.1
- Fabric: Loader 0.18.4+
- NeoForge: 21.1.174+
