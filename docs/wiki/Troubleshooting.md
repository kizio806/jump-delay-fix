# Troubleshooting

## Mod is not loading

- Verify that you are running **Java 25**.
- Check that you have the correct Fabric API or NeoForge version installed.
- Verify the JAR filename matches your Minecraft version line (26.1.x).

## Jump feels the same as vanilla

- Press `H` to ensure Jump Delay Fix is toggled **ON**.
- Check settings via `K` to ensure the mod is active.

## Rubberbanding or movement lag on multiplayer servers

- Switch profile to **Stable Anti-Rubberband** using `J` or via the settings screen (`K`).
- Ensure Auto Profile Switching is turned ON so the mod automatically adjusts to packet latency.

## Config file location and resetting

```
.minecraft/config/jumpdelayfix.properties
```

To reset settings:
- Use the **Reset to Defaults** button in the settings screen (`K`), or
- Delete `.minecraft/config/jumpdelayfix.properties` while the game is closed.

## Build fails locally

Run:

```bash
./gradlew clean buildAll strictCheck --stacktrace
```

## Reporting Issues

Open an issue at [github.com/kizio806/jump-delay-fix/issues](https://github.com/kizio806/jump-delay-fix/issues) with:

- Minecraft version
- Loader (Fabric / NeoForge) and version
- Full crash log or latest.log
