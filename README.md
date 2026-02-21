# Jump Delay Fix

`Jump Delay Fix` is a client-side Minecraft mod that removes jump delay for instant jump chaining and better movement responsiveness.

## Loaders

- Fabric Loader
- NeoForge

## Supported Minecraft Versions

- Current build target is controlled by `gradle.properties` (`minecraft_version`).
- Default repository target: `1.21.1`.
- Release process supports sequential updates for newer `1.21.x` versions.

## Optional Compatibility

The mod has no hard dependency on optimization or recipe mods and is designed to run safely with:

- Sodium
- Iris
- Lithium
- JEI
- REI
- EMI

## Features

- Removes client-side jump delay while jump key is held
- Uses a multiplayer-safe ground stabilization tick to reduce rubberbanding
- Smart Adaptive Engine: automatically increases/decreases safety buffer after accepted/rejected jump attempts
- 3 live profiles: `Competitive`, `Smart Adaptive`, `Stable Anti-Rubberband`
- Runtime toggle (`J` by default)
- Profile cycle key (`H` by default)
- Settings GUI (`O` by default) with live toggles and direct profile switching
- Dedicated HUD Editor screen (drag position, move buttons, scale controls)
- Per-stat HUD visibility toggles (show/hide profile, rollback, mode, server, bar)
- Runtime tuning for rollback thresholds and auto-switch sample size
- Shareable preset import/export code system
- Per-server profile memory with one-click reset
- Telemetry HUD with rollback quality bar and adaptive diagnostics
- Action-bar status feedback (`ON/OFF`)
- Zero required optional dependencies
- Dedicated server safe packaging (no client classloading in NeoForge entrypoint)

## Project Architecture

- `common/`:
  - shared logic (`JumpDelayFix`, state, jump handler)
  - common bootstrap and registration pipeline stubs
  - unit tests
- `fabric/`:
  - Fabric client bootstrap
  - Fabric registry/event/network/datagen adapters
- `neoforge/`:
  - NeoForge entrypoint + client bootstrap split
  - DeferredRegister-based registry layer
  - NeoForge event/network/datagen adapters

## Build

Requirements:

- Java `21`
- Gradle wrapper (`./gradlew`)

Commands:

```bash
./gradlew :fabric:build
./gradlew :neoforge:build
./gradlew buildAll
```

Artifacts:

- `fabric/build/libs/jumpdelayfix-fabric-<mc>-<version>.jar`
- `neoforge/build/libs/jumpdelayfix-neoforge-<mc>-<version>.jar`

## Verification

```bash
./gradlew :common:test
./gradlew publishReadyCheck
```

`publishReadyCheck` validates:

- metadata files
- icon size (`512x512`)
- NeoForge dedicated-server safety guard
- SemVer validation for `mod_version`

## Sequential Release Workflow (Next Minecraft Versions)

Use the helper script to move the project to the next MC version:

```bash
./scripts/set-minecraft-version.sh \
  --minecraft 1.21.2 \
  --mod-version 1.1.1 \
  --yarn 1.21.2+build.1 \
  --fabric-api 0.103.0+1.21.2 \
  --neoforge 21.2.10 \
  --modrinth-game-versions "1.21.2"
```

Then run:

```bash
./gradlew --no-daemon clean buildAll publishReadyCheck
git add gradle.properties
```

Commit and tag:

```bash
git commit -m "chore(release): prepare MC 1.21.2 and v1.1.1"
git tag v1.1.1
git push origin HEAD --tags
```

`release.yml` reads `minecraft_version` and `modrinth_game_versions` directly from `gradle.properties`, so you do not need to edit workflow YAML for every new MC version.

## CI / Release

- CI workflow: `.github/workflows/ci.yml`
- Release workflow: `.github/workflows/release.yml`
- Changelog file: `CHANGELOG.md`

## License

Apache-2.0
