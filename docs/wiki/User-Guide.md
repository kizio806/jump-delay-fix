# User Guide

## In-Game Controls

| Action | Default Key |
|--------|-------------|
| Toggle mod on/off | `H` |
| Cycle jump profile | `J` |
| Open settings screen | `K` |

> Keys can be rebound in Minecraft's **Options → Controls → Key Binds** screen under the **Jump Delay Fix** category.

## Jump Profiles

| Profile | Description |
|---------|-------------|
| `Smart Adaptive` | Balanced responsive jumping. Automatically balances grounded timing against server latency and rollback detection. |
| `Stable Anti-Rubberband` | Conservative timing with higher grounded frames. Ideal for high ping or servers with strict anti-cheat movement checks. |
| `Competitive` | Lowest grounded delay before triggering jump. Designed for low latency connections (<95ms) and responsive movement. |

## Settings Screen

Access the settings screen via:
- Keybind: Press `K` in-game.
- On Fabric with **ModMenu** installed: **Mods → Jump Delay Fix → Configure**.
- NeoForge: Via the registered mod settings screen.

The settings screen allows you to:
- Toggle mod enabled / disabled.
- Cycle manual jump profile.
- Enable or disable Auto Profile switching.
- **Reset to Defaults** to restore factory settings.

## Configuration Persistence

Configuration and per-server profile memories are stored in:

```
.minecraft/config/jumpdelayfix.properties
```
