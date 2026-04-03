# Changelog

All notable changes to this project should be documented in this file.

## 1.0.0

### Added
- Shared bootstrap service contracts and registry abstractions so Fabric and NeoForge can implement platform behavior without leaking loader-specific imports into `common`.
- Headless GameTest coverage and stricter publication checks for structure, metadata, runtime dependencies, and dedicated-server safety.
- Repository-facing documentation for releases, Modrinth publication, wiki navigation, and project conventions.

### Changed
- Simplified the shipped feature set around jump control, manual profiles, and adaptive auto profile switching.
- Reworked Fabric and NeoForge bootstrap wiring around platform-specific adapters while keeping shared logic in `common`.
- Cleaned repository metadata, release automation, and docs to reflect the actual published product scope.

### Removed
- Stale HUD, datagen, networking, and accessor-style integration paths that no longer matched the maintained architecture.
