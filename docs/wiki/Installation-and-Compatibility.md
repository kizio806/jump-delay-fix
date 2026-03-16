# Installation and Compatibility

## Requirements

- Minecraft Java Edition
- Java `21`
- `Fabric Loader` + Fabric API, or `NeoForge`

## Current Compatibility

- Active patch line: `1.21.x`
- Validated game versions: `1.21.9`, `1.21.10`, `1.21.11`
- Fabric range in metadata: `>=1.21.9 <1.21.12`
- NeoForge range in metadata: `[1.21,1.22)`

If you try to run on a newer patch that is not mapped/available yet, the build and runtime metadata checks will block release.

## One-JAR Multi-Version Strategy

This project uses one release for multiple patch versions in the same Minecraft line whenever APIs and mappings allow it.

The strategy is enforced by Gradle checks:

- `verifyMinecraftVersionMatrix`
- `publishReadyCheck`

## Install from Modrinth

1. Open https://modrinth.com/mod/jump-delay-fix
2. Choose your Minecraft version and loader
3. Download the latest compatible file
4. Put the jar in your `mods` folder
5. Start Minecraft

## Install from GitHub Releases

1. Open https://github.com/kizio806/jump-delay-fix/releases
2. Pick the latest release
3. Download the correct loader artifact (`jumpdelayfix-fabric-...jar` or `jumpdelayfix-neoforge-...jar`)
4. Put the jar in `mods`
5. Start Minecraft

## Verify Correct Setup

- Mod appears in the mods list
- Keybinds are visible in controls under `Jump Delay Fix`
- Default keys respond in-game (`J` toggle, `H` profile cycle, `O` settings)

## Common Installation Mistakes

- Wrong loader jar for your instance
- Missing loader/API dependency
- Unsupported Minecraft patch version
- Outdated Java runtime (must be Java 21)
