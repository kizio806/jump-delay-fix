# Jump Delay Fix

[![CI](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml/badge.svg)](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver)](https://github.com/kizio806/jump-delay-fix/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/jump-delay-fix?logo=modrinth&label=modrinth%20downloads)](https://modrinth.com/mod/jump-delay-fix)
[![Discord](https://img.shields.io/badge/Discord-Join%20Server-5865F2?logo=discord)](https://discord.com/invite/M9eqfP49Yy)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix)](LICENSE)

Lightweight client-side Minecraft mod focused on smoother and more responsive jumping while staying practical for normal gameplay, including multiplayer.

The repository follows the same GitHub-facing structure used across `item-display-control` and `event-hub`: explicit maintainer metadata, top-level technical docs, `docs/wiki` source pages, and release/publication guides kept inside the repo.

## Highlights

- Improved jump responsiveness with multiplayer-friendly behavior
- In-game toggle with simple Minecraft-style settings screens
- Manual profile cycling for quick per-server adjustments
- Optional auto profile switching based on rollback history and latency
- Client-only implementation for Fabric and NeoForge

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
Fabric support can still be narrower than NeoForge support when upstream Fabric dependencies are locked to one specific patch.

## Architecture

Multi-project Gradle layout:

- `common`: loader-agnostic jump logic, config/state, shared API, bootstrap contracts, and registration object definitions exposed as suppliers
- `fabric`: Fabric bootstrap, key bindings, screens, networking, and headless GameTest wiring
- `neoforge`: NeoForge bootstrap, key bindings, screens, networking, and headless GameTest wiring

Platform modules implement the client-facing interfaces from `common/api` and provide the concrete block/item registry bridges used during bootstrap. This keeps the shared module free from loader-specific imports while letting each platform own its own registration mechanics and client hooks.

Data flow:

```text
common/api + common/feature + common/config
                |
                v
        CommonBootstrapServices
           /               \
          v                 v
   fabric adapters     neoforge adapters
          |                 |
          v                 v
   Minecraft Fabric     Minecraft NeoForge
```

Design goals:

- Shared jump-delay logic stays in `common`
- `common` exposes the shared API, while Fabric and NeoForge implement the client/service interfaces and registries
- Platform modules own all client-only entrypoints, screens, HUD hooks, and key bindings
- Dedicated-server-safe bootstrap keeps `common` free from `net.fabricmc` and `net.neoforged` imports
- The runtime fix does not mutate private vanilla jump-cooldown fields, so no accessor/invoker mixins are required

## Build

Requirements:

- Java `21`
- Minecraft branch baseline for this architecture: `1.21.1` to `1.21.4`
- Fabric Loom `1.7+`
- NeoGradle `7.0+`
- Gradle wrapper (`./gradlew`)

Repository note:

- Checked-in `gradle.properties` stays pinned for reproducible CI and release automation

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
2. Generate release notes from the matching `CHANGELOG.md` section
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
  --fabric-supported-versions "1.21.11" \
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

Current validated Fabric target is `1.21.11`.
NeoForge metadata still covers the `1.21.x` patch line.
`1.21.12+` currently fails because that Minecraft patch is not yet available in Loom setup (`Failed to find minecraft version`).

## Modrinth Project Content

The project description template and publishing notes are in `docs/MODRINTH.md`.

## Project Docs

- Technical docs index: [docs/Home.md](docs/Home.md)
- Wiki home: [docs/wiki/Home.md](docs/wiki/Home.md)
- Code style: [docs/Code-Style.md](docs/Code-Style.md)
- Release guide: [docs/RELEASES.md](docs/RELEASES.md)
- Modrinth guide: [docs/MODRINTH.md](docs/MODRINTH.md)
- Changelog: [CHANGELOG.md](CHANGELOG.md)
- Contributing: [CONTRIBUTING.md](CONTRIBUTING.md)
- Security: [SECURITY.md](SECURITY.md)
