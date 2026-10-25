# Installation Guide

Jump Delay Fix is a client-side mod compatible with both Fabric and NeoForge on Minecraft **26.3**.

## Requirements

- **Minecraft Java Edition:** 26.3
- **Java Runtime:** Java 25 (bundled with official launcher for 26.x)
- **Mod Loader:** Either [Fabric Loader](https://fabricmc.net/) (>= 0.18.4) or [NeoForge](https://neoforged.net/) (>= 26.3.0.1-beta)

## Fabric Installation

1. Install **Fabric Loader** for Minecraft 26.3 using the official installer.
2. Download matching **Fabric API** from [Modrinth](https://modrinth.com/mod/fabric-api) or CurseForge and place it into your `.minecraft/mods` directory.
3. *(Optional but recommended)* Install **[ModMenu](https://modrinth.com/mod/modmenu)** to configure settings directly from the in-game mods list.
4. Download `jumpdelayfix-fabric-26.3-1.4.1.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
5. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
6. Launch Minecraft using your Fabric profile.

## NeoForge Installation

1. Install **NeoForge** for Minecraft 26.3 using the NeoForge installer.
2. Download `jumpdelayfix-neoforge-26.3-1.4.1.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
3. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
4. Launch Minecraft using your NeoForge profile.

## Server Compatibility & Multiplayer Policy

- **Client-Side Only:** Jump Delay Fix does NOT need to be installed on servers.
- **Fair-Play:** The mod eliminates client-side input hesitation when holding jump; it respects all server-side physics, movement tick rates, and anti-cheat constraints.
- When playing on competitive servers, switch to the `Stable Anti-Rubberband` profile or ensure auto-switch is enabled so the mod adapts to server packet rates.
