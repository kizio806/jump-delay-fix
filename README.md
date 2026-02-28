# Jump Delay Fix

[![CI](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml/badge.svg)](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver)](https://github.com/kizio806/jump-delay-fix/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/jump-delay-fix?logo=modrinth&label=modrinth%20downloads)](https://modrinth.com/mod/jump-delay-fix)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix)](LICENSE)

Client-side Minecraft mod that improves jump responsiveness while preserving multiplayer stability.

## Highlights

- Client-only implementation (no gameplay authority moved to server)
- Fabric + NeoForge support from one repository
- One-JAR release strategy for one Minecraft patch line
- Adaptive profiles (`Competitive`, `Smart`, `Stable`) with rollback-aware switching
- Runtime HUD + settings screens + preset import/export

## Supported Platforms

- Fabric Loader
- NeoForge

Current version metadata is managed in `gradle.properties`:

- `minecraft_version`: base compile target
- `fabric_game_versions`: Fabric validated game versions
- `neoforge_game_versions`: NeoForge validated game versions
- `modrinth_game_versions`: shared Fabric/NeoForge versions published on Modrinth
- `fabric_minecraft_version_range`: Fabric runtime support range
- `minecraft_version_range`: NeoForge runtime support range

The build enforces a single patch line (for example `1.21.x`) so one release can target multiple patch versions when technically compatible.

## Architecture

Multi-project Gradle layout:

- `common`: loader-agnostic jump logic, config, presets, diagnostics, HUD presentation model
- `fabric`: Fabric entrypoints and rendering/input adapters
- `neoforge`: NeoForge entrypoints and rendering/input adapters

Design goals:

- Shared behavior in `common`
- Thin loader integration layers
- No dedicated server classloading from client-only features

## Build

Requirements:

- Java `21`
- Gradle wrapper (`./gradlew`)

Main commands:

```bash
./gradlew --no-daemon clean buildAll
./gradlew --no-daemon publishReadyCheck
```

`publishReadyCheck` runs:

- SemVer validation (`mod_version`)
- Minecraft version matrix validation
- Common tests
- Fabric + NeoForge metadata checks
- NeoForge dedicated-server safety check

## Release Workflow

Tag-based release (`vX.Y.Z`) is fully automated by `.github/workflows/release.yml`:

1. Build + verify artifacts
2. Generate release changelog from git history
3. Publish GitHub Release with Fabric/NeoForge jars + SHA256 checksums
4. Publish to Modrinth (if secrets are configured)

Required repository secrets:

- `MODRINTH_TOKEN`
- `MODRINTH_PROJECT_ID`

## Multi-Version Release Process

Probe candidate versions before extending metadata:

```bash
./scripts/probe-minecraft-versions.sh \
  --versions "1.21.9,1.21.10,1.21.11,1.21.12"
```

Use the helper script to update one patch-line release metadata consistently:

```bash
./scripts/set-minecraft-version.sh \
  --base-minecraft 1.21.11 \
  --fabric-supported-versions "1.21.9,1.21.10,1.21.11" \
  --neoforge-supported-versions "1.21.9,1.21.10,1.21.11" \
  --mod-version 1.0.0
```

The script updates:

- `minecraft_version`
- `fabric_game_versions` (sorted + deduplicated)
- `neoforge_game_versions` (sorted + deduplicated)
- `modrinth_game_versions` (shared intersection, sorted + deduplicated)
- `fabric_minecraft_version_range` (auto-derived)
- `minecraft_version_range` (auto-derived)

Then verify and release:

```bash
./gradlew --no-daemon clean buildAll publishReadyCheck
git add -A
git commit -m "chore(release): v1.0.0"
git tag v1.0.0
git push origin main --tags
```

Current validated upper bound from the `1.21.9` line is `1.21.11`.
`1.21.12+` currently fails because that Minecraft patch is not yet available in Loom setup (`Failed to find minecraft version`).

## Modrinth Project Content

The project description template and publishing notes are in `docs/MODRINTH.md`.

## Project Standards

- Contribution rules: `CONTRIBUTING.md`
- Security policy: `SECURITY.md`
- Changelog: `CHANGELOG.md`
- License: `LICENSE`
