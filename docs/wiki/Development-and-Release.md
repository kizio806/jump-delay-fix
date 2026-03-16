# Development and Release

## Repository Layout

- `common` -> loader-agnostic logic, config, profiles, diagnostics, HUD model
- `fabric` -> Fabric bootstrap, keybinds, overlays, UI integration
- `neoforge` -> NeoForge bootstrap, keybinds, overlays, UI integration
- `scripts` -> version probing and metadata update helpers
- `.github/workflows` -> CI and release automation

## Local Build

Requirements:

- Java `21`
- Gradle wrapper

Build commands:

```bash
./gradlew --no-daemon clean buildAll
./gradlew --no-daemon publishReadyCheck
```

`publishReadyCheck` includes:

- SemVer validation (`mod_version`)
- Minecraft version matrix validation
- Common tests
- Fabric metadata validation
- NeoForge metadata validation
- NeoForge dedicated server safety validation

## Multi-Version Update Flow

1. Probe candidate Minecraft versions:

```bash
./scripts/probe-minecraft-versions.sh --versions "1.21.9,1.21.10,1.21.11,1.21.12"
```

2. Update metadata consistently:

```bash
./scripts/set-minecraft-version.sh \
  --base-minecraft 1.21.11 \
  --fabric-supported-versions "1.21.9,1.21.10,1.21.11" \
  --neoforge-supported-versions "1.21.9,1.21.10,1.21.11" \
  --mod-version 1.0.0
```

3. Verify:

```bash
./gradlew --no-daemon clean buildAll publishReadyCheck
```

## Release Process

Release is tag-driven using `.github/workflows/release.yml`.

1. Update `CHANGELOG.md`
2. Commit changes
3. Create tag `vX.Y.Z`
4. Push tag

Example:

```bash
git add -A
git commit -m "chore(release): v1.0.0"
git tag v1.0.0
git push origin main --tags
```

Pipeline steps:

- build + verification
- changelog generation from git history
- GitHub Release with Fabric/NeoForge jars and checksums
- Modrinth publish (when secrets exist)

## Required Secrets

- `MODRINTH_TOKEN`
- `MODRINTH_PROJECT_ID`

No secrets should be hardcoded in repository files.

## Versioning Policy

- Semantic Versioning: `MAJOR.MINOR.PATCH`
- Use `PATCH` for fixes, small compatibility updates
- Use `MINOR` for backward-compatible feature additions
- Use `MAJOR` for breaking behavior/config changes
