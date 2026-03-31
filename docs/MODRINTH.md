# Modrinth Publishing Guide

## Description Template

Jump Delay Fix is a lightweight client-side mod focused on smoother, more responsive jumping and multiplayer-friendly behavior.

### Features

- Improved jump responsiveness for everyday play
- In-game ON/OFF toggle and clean settings GUI
- Manual profile cycling for quick per-server tuning
- Optional auto profile switching based on rollback history and latency
- Client-side only, with no server installation required

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
