# User Guide

## In-Game Controls

| Action | Default Key |
|--------|-------------|
| Toggle mod on/off | `J` |
| Cycle jump profile | `H` |
| Open settings screen | `O` |

> Keys can be rebound in Minecraft's **Options → Controls → Key Binds** screen.

## Jump Profiles

| Profile | Description |
|---------|-------------|
| `SMART` | Adaptive mode. Automatically balances timing and responsiveness based on server latency. Recommended for most players. |
| `COMPETITIVE` | Fast response mode for low-latency (< 95ms) environments and competitive parkour. |
| `STABLE` | Conservative timing mode with extra grounding safety to eliminate rubberbanding on laggy servers. |

## Settings Screen

Access the settings screen via:
- In-game keybind: **O** (default)
- On Fabric: **Mods → Jump Delay Fix → Configure** (requires ModMenu)
- On NeoForge: **Mods → Jump Delay Fix → Config**

The settings screen allows you to:
- Toggle the mod ON / OFF.
- Cycle through jump profiles.
- Toggle automatic profile switching.
- Reset all settings to defaults.

## Server Profiles

The mod automatically remembers the profile selected for each server you connect to. Profiles are stored in:

```
.minecraft/config/jumpdelayfix.properties
```
