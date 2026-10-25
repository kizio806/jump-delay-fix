# Installation Guide

Jump Delay Fix is a client-side Minecraft mod available for both **Fabric** and **NeoForge**. No server-side installation is needed.

## Supported Versions

- **Minecraft:** Fabric: `1.21.11`; NeoForge: `1.21.11`
- **Java:** 21 (bundled with official launcher for 1.21.x)
- **Fabric Loader:** 0.18.4+ / Fabric API `0.141.6+1.21.11`
- **NeoForge:** 21.11.48+ (loader range `[21.11,)`, MC range `[1.21, 1.22)`)

---

## Fabric Installation

1. **Install Fabric Loader:**
   - Download and install the Fabric Installer from [fabricmc.net](https://fabricmc.net/).
   - Select Minecraft version `1.21.11`.

2. **Install Dependencies:**
   - Download **Fabric API** (`0.141.6+1.21.11` or newer) and place the `.jar` in your `.minecraft/mods` folder.
   - *(Optional)* Install **Mod Menu** to access in-game mod settings from the main menu.

3. **Install Jump Delay Fix:**
   - Download `jumpdelayfix-fabric-1.21.11-1.1.1.jar` from Modrinth or GitHub Releases.
   - Place the `.jar` into `.minecraft/mods`.

4. **Launch the Game:**
   - Select the Fabric profile in the Minecraft launcher and start the game.

---

## NeoForge Installation

1. **Install NeoForge:**
   - Download and run the NeoForge installer for Minecraft 1.21.11 (build `21.11.48` or newer) from [neoforged.net](https://neoforged.net/).

2. **Install Jump Delay Fix:**
   - Download `jumpdelayfix-neoforge-1.21.11-1.1.1.jar` from Modrinth or GitHub Releases.
   - Place the `.jar` into `.minecraft/mods`.

3. **Launch the Game:**
   - Select the NeoForge profile in the Minecraft launcher and start the game.

---

## Verification

Once in-game, you can verify that the mod is active:
- Press **J** (default) to toggle Jump Delay Fix ON/OFF. An action bar message will confirm the state.
- Press **H** (default) to cycle through jump profiles (Smart Adaptive, Competitive, Stable Anti-Rubberband).
- Press **O** (default) to open the configuration screen.

## Server Notice & Disclaimer

> [!NOTE]
> Jump Delay Fix operates purely on the client side. While it provides smoother jumping by modifying the client-side jump cooldown, it does not bypass server-side movement checks. Server administrators and anti-cheat plugins have distinct movement policies; always verify that client movement mods are permitted on the servers you play on.
