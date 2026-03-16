# Jump Delay Fix Wiki

Welcome to the official wiki for **Jump Delay Fix**.

Jump Delay Fix is a client-side movement quality-of-life mod for Minecraft that improves jump responsiveness while preserving multiplayer safety and server compatibility.

[![CI](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml/badge.svg)](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver)](https://github.com/kizio806/jump-delay-fix/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/jump-delay-fix?logo=modrinth&label=modrinth%20downloads)](https://modrinth.com/mod/jump-delay-fix)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix)](https://github.com/kizio806/jump-delay-fix/blob/main/LICENSE)

## Quick Navigation

- [Installation and Compatibility](Installation-and-Compatibility.md)
- [User Guide](User-Guide.md)
- [Development and Release](Development-and-Release.md)
- [Troubleshooting](Troubleshooting.md)

## Project Snapshot

- Mod ID: `jumpdelayfix`
- Supported loaders: `Fabric`, `NeoForge`
- Java: `21`
- Current active Minecraft patch line: `1.21.x`
- Current validated support range: `1.21.9-1.21.11`
- Current mod version: `1.0.0`

Version metadata is managed in `gradle.properties` and validated during build by `publishReadyCheck`.

## What Makes This Mod Different

- Fully client-side implementation
- Shared logic in `common` with thin loader integrations (`fabric`, `neoforge`)
- One-release, multi-patch strategy within one Minecraft line when technically valid
- Adaptive profiles with rollback-aware switching
- In-game settings, HUD editor, and preset import/export

## Keybind Defaults

- `J` -> toggle Jump Delay Fix
- `H` -> cycle profile
- `O` -> open settings screen

## Important Links

- Modrinth project: https://modrinth.com/mod/jump-delay-fix
- GitHub repository: https://github.com/kizio806/jump-delay-fix
- Release process details: https://github.com/kizio806/jump-delay-fix/blob/main/docs/RELEASES.md
- Modrinth publishing template: https://github.com/kizio806/jump-delay-fix/blob/main/docs/MODRINTH.md
- Changelog: https://github.com/kizio806/jump-delay-fix/blob/main/CHANGELOG.md
