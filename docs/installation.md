# Installation Guide

Jump Delay Fix is a client-side mod compatible with both Fabric and NeoForge.

---

## ☕ System Requirements

- **Minecraft Java Edition:** Supported versions include 1.21.9–1.21.11, 26.1.x, 26.2, and 26.3.
- **Java Runtime:**
  - Minecraft 26.x: **Java 25** (bundled by modern launchers)
  - Minecraft 1.21.x: **Java 21**
- **Mod Loader:** Either [Fabric Loader](https://fabricmc.net/) (>= 0.18.4) or [NeoForge](https://neoforged.net/)

---

## 📥 Fabric Installation

1. Install **Fabric Loader** for your target Minecraft version using the official installer.
2. Download the matching **Fabric API** from [Modrinth](https://modrinth.com/mod/fabric-api) and place it into your `.minecraft/mods` directory.
3. *(Optional but recommended)* Install **[ModMenu](https://modrinth.com/mod/modmenu)** to configure settings directly from the in-game mods list.
4. Download `jumpdelayfix-fabric-<mc>-<version>.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
5. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
6. Launch Minecraft using your Fabric profile.

---

## 📥 NeoForge Installation

1. Install **NeoForge** for your target Minecraft version using the official installer.
2. Download `jumpdelayfix-neoforge-<mc>-<version>.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
3. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
4. Launch Minecraft using your NeoForge profile. Settings can be accessed via the **Mods** menu or directly with the in-game hotkey (**`O`**).

---

## 🌐 Server Compatibility & Fair Play Policy

- **Client-Side Only:** Jump Delay Fix does NOT need to be installed on servers. It works when joining standard vanilla, Paper, Purpur, or modded servers.
- **Fair Play:** The mod eliminates client-side input hesitation when holding jump; it respects all server-side physics, movement tick rates, and anti-cheat constraints.
- When playing on high-ping servers, switch to the `STABLE` profile (press **`H`**) or ensure auto-switch is enabled so the mod adapts to server packet rates.
