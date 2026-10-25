# Modrinth Project & Publishing Guide

This document contains the official description markdown and release metadata for Jump Delay Fix on [Modrinth](https://modrinth.com/mod/jump-delay-fix).

---

## 📋 Modrinth Project Description

*(Copy the markdown below into the Modrinth project description editor)*

```markdown
# ⚡ Jump Delay Fix

**Jump Delay Fix** is a lightweight, **100% client-side** Minecraft mod designed to eliminate the artificial vanilla jump delay for smoother, instant, and hyper-responsive movement. Built for casual survival, precision parkour, and competitive PvP!

---

## ✨ Features

- 🚀 **Instant Jump Response:** Eliminates the vanilla hardcoded 10-tick delay when holding the jump key after landing, making consecutive jumps fluid and seamless.
- ⚡ **Multi-Loader Support:** Fully native builds for both **Fabric** and **NeoForge**.
- 🧠 **Smart Adaptive Tuning:** Continuously monitors server latency and rollback history to adapt jump timing dynamically, avoiding rubberbanding on strict or laggy servers.
- 💾 **Per-Server Profile Memory:** Automatically remembers and applies your preferred profile for each server or world you join (caches up to 128 servers).
- ⚙️ **In-Game Settings GUI:** Integrated directly into **ModMenu** (Fabric) and **Mod Options** (NeoForge), or opened via hotkey (`O`), complete with a one-click **"Reset to Defaults"** button.
- 💬 **Actionbar HUD Notifications:** Clear, non-intrusive actionbar alerts whenever you toggle the mod or switch profiles, translated into 10 languages.
- 🛡️ **100% Client-Side & Fair Play:** No server installation required—works on vanilla and modded servers alike without sending illegal movement packets.

---

## 🎮 Jump Profiles

Jump Delay Fix offers three finely calibrated profiles:

| Profile | Ground Latency Gate | Target Network Latency | Recommended For |
| :--- | :---: | :---: | :--- |
| **`SMART`** *(Default)* | Dynamic (0 ticks baseline) | Standard (0 – 200 ms) | **Everyday play & general servers.** Automatically adapts timing based on rollback rate and ping. |
| **`COMPETITIVE`** | -1 tick (Ultra-low latency) | Ping < 95 ms, rollback < 8% | **PvP, Bedwars, parkour & low-ping servers.** Fires jump inputs as early as ground contact is verified. |
| **`STABLE`** | +1 tick (Anti-rubberband) | Ping > 210 ms, rollback > 30% | **High-ping connections & strict anti-cheat servers.** Adds conservative ground verification to guarantee zero lagbacks. |

> 💡 **Auto Profile Switching:** When enabled, the mod dynamically steps between `COMPETITIVE`, `SMART`, and `STABLE` depending on real-time server conditions. You can toggle this feature at any time in the settings menu.

---

## ⌨️ Controls & Hotkeys

All keybindings can be customized in Minecraft's **Options → Controls → Key Binds** under the **Jump Delay Fix** category:

| Action | Default Key | Translation Key | Description |
| :--- | :---: | :--- | :--- |
| **Toggle Mod** | `J` | `key.jumpdelayfix.toggle` | Instantly turn Jump Delay Fix on or off in-game |
| **Cycle Profile** | `H` | `key.jumpdelayfix.profile` | Quickly cycle between `SMART`, `COMPETITIVE`, and `STABLE` |
| **Open Settings** | `O` | `key.jumpdelayfix.config` | Open the Jump Delay Fix configuration screen |

---

## 🛡️ Multiplayer & Anti-Cheat Compatibility

Jump Delay Fix is safe and transparent for multiplayer:

- **No Spoofed Movement:** The mod never sends impossible movement packets, alters jump trajectory, or grants illegitimate vertical velocity.
- **Client Input Simulation:** It executes the standard vanilla jump routine (`player.jumpFromGround()`) exactly when the client establishes valid ground contact.
- **Anti-Cheat Safe:** Compatible with server anti-cheats including **GrimAC**, **Vulcan**, **Polar**, **Matrix**, **Intave**, and **NCP**.
- **Self-Healing Fallback:** If a server experiences sudden tick-rate drops or network jitter, the mod's built-in rollback watcher automatically backs off into `STABLE` profile to eliminate rubberbanding.

---

## 📦 Supported Minecraft Versions

| Release Line | Mod Version | Supported Minecraft Versions | Mod Loaders | Java Baseline |
| :--- | :---: | :--- | :--- | :---: |
| **26.3** | `1.4.1` | **26.3** | Fabric 0.18.4+ / NeoForge 26.3.0.1-beta+ | Java 25 |
| **26.2** | `1.3.1` | **26.2** | Fabric 0.18.4+ / NeoForge 26.2.0.79+ | Java 25 |
| **26.1** | `1.2.1` | **26.1**, **26.1.1**, **26.1.2** | Fabric 0.18.4+ / NeoForge 26.1.2.103+ | Java 25 |
| **1.21** | `1.1.1` | **1.21.9**, **1.21.10**, **1.21.11** | Fabric 0.18.4+ / NeoForge 21.9.16+, 21.10.64+, 21.11.48+ | Java 21 |

---

## 📥 Installation

1. Ensure you have the corresponding mod loader installed (**Fabric** + Fabric API, or **NeoForge**).
2. Download the matching JAR for your loader and Minecraft version from the Versions tab.
3. Drop the JAR into your `.minecraft/mods` folder and start Minecraft.
```

---

## ⚙️ Project Metadata & Settings

- **Environment:**
  - Client: **Required**
  - Server: **Unsupported** *(Client-side only, no server installation needed)*
- **Categories / Tags:**
  - `Quality of Life`
  - `Utility`
  - `Movement`
  - `PvP`
- **License:** Apache-2.0
- **Source Code Repository:** `https://github.com/kizio806/jump-delay-fix`
- **Issue Tracker:** `https://github.com/kizio806/jump-delay-fix/issues`
- **Wiki:** `https://github.com/kizio806/jump-delay-fix/wiki`

---

## 🚀 Release Workflow Integration

Modrinth publishing is automated via GitHub Actions (`.github/workflows/release.yml`) using the `Kir-Antipov/mc-publish` action with repository secrets:
- `MODRINTH_TOKEN`: Personal Access Token with project edit/upload scope.
- Game versions are read dynamically from `modrinth_game_versions` in `gradle.properties`.
