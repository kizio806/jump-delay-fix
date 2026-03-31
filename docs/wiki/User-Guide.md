# User Guide

## Default Keybinds

- `J` -> Toggle Jump Delay Fix
- `H` -> Cycle jump profile
- `O` -> Open settings

## Profiles

Jump Delay Fix provides three profiles:

- `Competitive` -> fastest feel, low rollback tolerance
- `Smart Adaptive` -> balanced default
- `Stable Anti-Rubberband` -> safer for unstable/high-latency servers

The profile can switch automatically based on measured rollback and ping, if auto switching is enabled.

## Main Settings

- Enable/disable mod
- Cycle jump profile manually
- Toggle Auto Profile switching on or off

Manual profile selection always disables auto switching for the current setup.
If you want the mod to manage profile changes again, turn Auto Profile back on in the settings screen.

## Configuration File

- File location: `config/jumpdelayfix.properties`
- Includes the Auto Profile toggle and per-server profile memory

Per-server memory lets the mod remember which profile was last used on each server.

## Safety Model

This mod is client-side only. It does not move gameplay authority to the server.

Safety mechanisms:

- rollback-aware adaptive penalties
- optional auto switching between `Competitive`, `Smart Adaptive`, and `Stable`
- per-server memory to avoid cross-server behavior bleed
