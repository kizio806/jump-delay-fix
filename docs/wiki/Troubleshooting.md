# Troubleshooting

## Mod Does Not Load

Check:

- You installed the correct loader artifact (Fabric vs NeoForge)
- Your Minecraft version is in the supported range
- Java runtime is version `21`
- Required loader/API dependencies are present

## Red Errors in IDE

If VS Code still shows red after updates:

1. Run `./gradlew --no-daemon clean buildAll`
2. Reload Gradle project in IDE
3. Ensure Java toolchain points to version `21`

If command-line build is green and IDE still highlights errors, this is usually an index/sync issue, not a runtime defect.

## Keybinds Do Nothing

Check:

- No key conflicts in Controls
- You are in a client world/session
- Mod is enabled (`J` toggle)

## Wrong Profile Behavior

Check in settings:

- `Auto Profile` is enabled if you expect auto switching
- `Safety Failsafe` is enabled if you want forced stable behavior on high rollback
- adaptive thresholds are not set too aggressively

You can always reset defaults in the settings screen.

## HUD Not Visible

Check:

- HUD is enabled in settings
- HUD position is on-screen
- HUD scale is not too small
- individual HUD sections are not disabled

Use HUD editor reset if needed.

## Release Workflow Failed

Checklist:

- Tag matches `mod_version` in `gradle.properties`
- `publishReadyCheck` passes locally
- Modrinth secrets are configured in GitHub repository settings
- Artifact names are unique and not duplicated in release assets

## Need Help

- Open issue: https://github.com/kizio806/jump-delay-fix/issues
- Include Minecraft version, loader/version, mod version, logs, and reproduction steps
