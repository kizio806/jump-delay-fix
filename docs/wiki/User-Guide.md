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

Core controls:

- Enable/disable mod
- Auto Profile switching
- Shadow Mode
- HUD Overlay
- Safety Failsafe

Profile/adaptive controls:

- Auto-switch sample size
- Competitive max rollback threshold
- Stable min rollback threshold
- Failsafe rollback threshold

Preset controls:

- Export preset code
- Import preset code
- Reset settings to defaults
- Clear per-server profile memory

## HUD Editor

HUD features include:

- Drag-and-drop position
- Precise move controls (up/down/left/right)
- Scale controls
- Section toggles (Profile + ping, Rollback + penalty, Mode + quality, Server line, Rollback bar)

Default HUD layout values:

- X offset: `6`
- Y offset: `6`
- Scale: `1.0`

HUD visibility and layout are persisted in config.

## Configuration File

- File location: `config/jumpdelayfix.properties`
- Includes runtime toggles, HUD layout visibility, adaptive thresholds, and per-server profile memory

Values are clamped defensively in runtime to avoid invalid state from manual edits.

## Safety Model

This mod is client-side only. It does not move gameplay authority to the server.

Safety mechanisms:

- rollback-aware adaptive penalties
- optional failsafe escalation to `Stable`
- per-server memory to avoid cross-server behavior bleed
