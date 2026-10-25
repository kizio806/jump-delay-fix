# Troubleshooting

## Mod is not loading

- Check that you have the correct Fabric API or NeoForge version installed.
- Verify the JAR filename matches your Minecraft version (e.g., `jumpdelayfix-fabric-26.3-1.4.1.jar`).
- Ensure you are running Java 25.

## Jump feels the same as vanilla

- Open the in-game settings screen (`K` key) and verify the mod is **enabled**.
- Verify you are holding down the jump key while moving forward.
- Try switching to the **Competitive** profile (`J` key).

## Settings screen not showing (Fabric)

- Install [ModMenu](https://modrinth.com/mod/modmenu) to access the settings screen from the mods list, or press the settings key (`K` by default).

## Resetting Configuration

- Open the settings screen (`K` by default) and click **Reset to Defaults** (`Przywróć domyślne`).
- Alternatively, delete `.minecraft/config/jumpdelayfix.properties` while the game is closed.

## Build fails locally

Run:

```bash
./gradlew clean buildAll strictCheck --stacktrace
```

## Reporting Issues

Open an issue at [github.com/kizio806/jump-delay-fix/issues](https://github.com/kizio806/jump-delay-fix/issues) with:

- Minecraft version (e.g. 26.3)
- Loader (Fabric / NeoForge) and exact loader version
- Full crash log or latest.log output
