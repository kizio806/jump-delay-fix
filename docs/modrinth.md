# ⚡ Jump Delay Fix

[![GitHub Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver&style=flat-square&color=00AF5C)](https://github.com/kizio806/jump-delay-fix/releases)
[![Loaders](https://img.shields.io/badge/Loaders-Fabric%20%7C%20NeoForge-blue?style=flat-square)](https://modrinth.com/mod/jump-delay-fix)
[![Environment](https://img.shields.io/badge/Environment-100%25%20Client--Side-brightgreen?style=flat-square)](https://modrinth.com/mod/jump-delay-fix)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix?style=flat-square&color=orange)](https://github.com/kizio806/jump-delay-fix/blob/main/LICENSE)

**Jump Delay Fix** is a lightweight, purely **client-side** Minecraft mod designed to eliminate vanilla jump delay for smoother, instant, and hyper-responsive movement. Built for casual survival, technical parkour, and competitive PvP!

In vanilla Minecraft, the client enforces a mandatory jump delay counter after touching the ground. Jump Delay Fix removes this artificial delay, giving you instantaneous control over your jumps while intelligently protecting against server lag-backs.

---

## ✨ Features

- 🚀 **Instant Jump Response:** Removes the vanilla hardcoded jump delay upon landing for immediate, fluid jumping.
- ⚡ **Multi-Loader Support:** Official builds available for both **Fabric** and **NeoForge**.
- 🧠 **Smart Auto-Tuning:** Dynamically analyzes server round-trip latency and rollback history to adapt jump timings on the fly.
- 💾 **Per-Server Profile Memory:** Automatically remembers and restores your preferred jump profile for each multiplayer server.
- ⚙️ **In-Game Settings GUI:** Seamlessly integrated into **ModMenu** (Fabric) and **Mod Options** (NeoForge), featuring a one-click **Reset to Defaults** option.
- 📊 **Visual Actionbar Feedback:** Displays subtle, color-coded status updates on the actionbar whenever you toggle the mod or switch profiles.
- 🛡️ **100% Client-Side:** No server-side mod installation required—works on vanilla, Paper, Purpur, and modded servers.

---

## 🎮 Jump Profiles

Jump Delay Fix includes three distinct tuning profiles to fit any playstyle or server environment:

| Profile | Timing Offset | Description | Recommended For |
| :--- | :---: | :--- | :--- |
| **`SMART`** *(Default)* | `0 ticks` | Dynamically balances immediate responsiveness with rollback mitigation based on real-time server latency. | **Everyday survival, parkour, and general multiplayer servers** |
| **`COMPETITIVE`** | `-1 tick` | Maximizes responsiveness with aggressive jump dispatch and tight latency validation. | **PvP servers, speedruns, and low-ping environments** |
| **`STABLE`** | `+1 tick` | Introduces conservative timing buffer with higher penalty recovery tolerance to prevent anti-cheat pushbacks. | **High-ping connections, cross-continental play, or strict anti-cheat servers** |

> 💡 *Enable **Auto-Profile Switching** in the settings GUI to allow the mod to automatically transition between profiles based on connection quality!*

---

## ⌨️ Controls & Hotkeys

All keybinds are fully customizable in Minecraft's **Options → Controls → Key Binds** under the **Jump Delay Fix** category:

| Action | Default Key | Description |
| :--- | :---: | :--- |
| **Toggle Mod On/Off** | `J` | Instantly enables or disables mod functionality with on-screen notification. |
| **Cycle Jump Profile** | `H` | Switches sequentially through `SMART` → `STABLE` → `COMPETITIVE`. |
| **Open Config GUI** | `O` | Opens the Jump Delay Fix settings screen directly without leaving your world. |

---

## 🧩 In-Game Settings Screen

Access the settings screen via keybind (`O`), through **ModMenu** (Fabric), or via the **Mod List** (NeoForge):

- **Mod State:** Toggle the mod `ON` or `OFF` at any time.
- **Active Profile:** View or cycle between `SMART`, `COMPETITIVE`, and `STABLE`.
- **Auto-Switch:** Enable or disable real-time latency/rollback auto-tuning.
- **Reset to Defaults:** Instantly restores factory settings if you ever need a clean baseline.

---

## 🛡️ Anti-Cheat & Multiplayer Safety

- Jump Delay Fix is strictly a **client-side input responsiveness utility**.
- It **never** alters vertical velocity values (`motion.y`), modifies jump height, accelerates movement speed, or sends illegal packets.
- The mod simply processes the client's jump input immediately upon confirmed ground collision, rather than waiting for vanilla's artificial post-landing cooldown.
- Jump Delay Fix respects your game state: jump attempts are safely inhibited during respawn transitions, player death, and sleeping.

---

## 📦 Requirements

| Loader | Required Dependencies | Recommended Optional Addons |
| :--- | :--- | :--- |
| **Fabric** | [Fabric Loader](https://fabricmc.net/) + [Fabric API](https://modrinth.com/mod/fabric-api) | [ModMenu](https://modrinth.com/mod/modmenu) (Settings GUI) |
| **NeoForge** | [NeoForge Loader](https://neoforged.net/) | — |

---

## 📥 Installation

1. Select your target Minecraft version and loader (**Fabric** or **NeoForge**) from the **Versions** tab.
2. Download the corresponding `.jar` file.
3. Place the downloaded file into your `.minecraft/mods` folder.
4. Launch the game and enjoy responsive movement!

---

## 📄 License & Links

- **Source Code:** [GitHub Repository](https://github.com/kizio806/jump-delay-fix)
- **Issue Tracker:** [GitHub Issues](https://github.com/kizio806/jump-delay-fix/issues)
- **Changelog:** [CHANGELOG.md](https://github.com/kizio806/jump-delay-fix/blob/main/CHANGELOG.md)
- **License:** [Apache License 2.0](https://github.com/kizio806/jump-delay-fix/blob/main/LICENSE)
