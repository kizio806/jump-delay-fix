# Modrinth Publishing Guide

## Description Template

Jump Delay Fix is a lightweight client-side mod focused on smoother, more responsive jumping and multiplayer-friendly behavior.

### Features

- Improved jump responsiveness for everyday play
- In-game ON/OFF toggle (`H`) and clean settings GUI (`K`)
- Manual profile cycling (`J`) for quick per-server tuning (Smart, Stable, Competitive)
- Reset to Defaults button for quick configuration restoration
- Optional auto profile switching based on rollback history and latency
- Client-side only, with no server installation required
- Multiplayer and fair-play compliant (respects all server tick physics and anti-cheat constraints)

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
