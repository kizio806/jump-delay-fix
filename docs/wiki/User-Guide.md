# User Guide

## In-Game Controls

| Action | Default Key |
|--------|-------------|
| Toggle mod on/off | `J` |
| Cycle jump profile | `K` |

> Keys can be rebound in Minecraft's **Options → Controls** screen.

## Jump Profiles

| Profile | Description |
|---------|-------------|
| `SMART` | Adaptive mode. Automatically selects the best behavior based on detected server rollback history. Recommended for most players. |
| `COMPETITIVE` | Stricter rollback checks. Best for competitive PvP servers with noticeable latency. |
| `VANILLA` | Disables all mod logic. Behaves identically to unmodded Minecraft. |

## Settings Screen

On Fabric with ModMenu installed, access settings via **Mods → Jump Delay Fix → Configure**.

On NeoForge, open **Options → Mod Options → Jump Delay Fix**.

The settings screen allows you to:

- Enable or disable the mod.
- Select your preferred jump profile.
- View current server adaptive stats.

## Server Profiles

The mod remembers the optimal profile for each server you connect to. Profiles are stored in:

```
.minecraft/config/jumpdelayfix/servers.json
```
