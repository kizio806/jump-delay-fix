# Troubleshooting

## Mod is not loading

- Check that you have the correct Fabric API or NeoForge version installed.
- Verify the JAR filename matches your Minecraft version.

## Jump feels the same as vanilla

- Open the in-game settings screen and verify the mod is **enabled**.
- Make sure the active profile is not set to **VANILLA**.

## Settings screen not showing (Fabric)

- Install [ModMenu](https://modrinth.com/mod/modmenu) to enable the settings screen on Fabric.

## Config file location

```
.minecraft/config/jumpdelayfix/config.json
.minecraft/config/jumpdelayfix/servers.json
```

Delete these files to reset to defaults.

## Build fails locally

Run:

```bash
./gradlew clean buildAll strictCheck --stacktrace
```

Check the output for specific error messages. Most issues are caused by mismatched Fabric API or NeoForge versions.

## Reporting Issues

Open an issue at [github.com/kizio806/jump-delay-fix/issues](https://github.com/kizio806/jump-delay-fix/issues) with:

- Minecraft version
- Loader (Fabric / NeoForge) and version
- Full crash log or error message
