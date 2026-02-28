# Modrinth Publishing Guide

## Description Template

Jump Delay Fix is a client-side movement quality-of-life mod focused on responsive jumping and multiplayer-safe behavior.

### Features

- Removes client-side jump delay while preserving multiplayer stability
- Adaptive jump profiles for different latency conditions
- Runtime toggle, profile cycle, and in-game settings screens
- Configurable HUD with drag editor and rollback quality indicator
- Preset export/import for quick sharing

### Compatibility

- Loader support: Fabric, NeoForge
- Published versions: defined by shared `modrinth_game_versions` (intersection of `fabric_game_versions` and `neoforge_game_versions`) in `gradle.properties`
- One release can target multiple patch versions within one Minecraft line when metadata ranges allow it

### Tags

Recommended tags:

- Client-side
- Utility
- QoL
- Movement

## Changelog Structure

Use this structure in release notes:

```markdown
## Version x.y.z

### Added
- ...

### Changed
- ...

### Fixed
- ...
```

## Release Integration

Modrinth publishing is handled by `.github/workflows/release.yml` using repository secrets.
