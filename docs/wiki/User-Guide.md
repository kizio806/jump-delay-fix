# User Guide

## In-Game Controls

| Action | Default Key |
|--------|-------------|
| Toggle mod on/off | `H` |
| Cycle jump profile | `J` |
| Open settings GUI | `K` |

> Keys can be rebound in Minecraft's **Options → Controls → Key Binds** screen under the **Jump Delay Fix** category.

## Jump Profiles

| Profile | Description |
|---------|-------------|
| `COMPETITIVE` | Zero delay. Player immediately jumps as soon as ground contact is detected. |
| `SMART` | Adaptive mode. Balances jump responsiveness with rollback detection. |
| `STABLE` | Anti-rubberband profile with conservative timing for high-ping or strict servers. |

## Settings Screen

Access settings via:
- In-game keybind (`K` by default).
- On Fabric: via **Mods → Jump Delay Fix → Configure** (requires [ModMenu](https://modrinth.com/mod/modmenu)).
- On NeoForge: via **Mods → Jump Delay Fix → Config**.

The settings screen allows you to:
- Toggle the mod ON or OFF.
- Toggle auto profile adaptation ON or OFF.
- Cycle through manual profiles (`Smart`, `Stable`, `Competitive`).
- **Reset to Defaults** (`Przywróć domyślne`) to restore factory settings with one click.

## Configuration File

Settings and server profile mappings are automatically persisted in:

```
.minecraft/config/jumpdelayfix.properties
```
