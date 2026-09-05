# Changelog

All notable changes to this project will be documented in this file.

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
