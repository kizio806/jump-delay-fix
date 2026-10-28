# Jump Delay Fix Wiki

Welcome to the official **Jump Delay Fix** documentation and wiki.

**Jump Delay Fix** is a lightweight, **100% client-side** Minecraft mod designed to eliminate the artificial jump delay for smoother, instant, and hyper-responsive movement on both **Fabric** and **NeoForge**.

---

## 📚 Wiki Directory

| Page | Description |
| :--- | :--- |
| **[User Guide](User-Guide.md)** | Controls (`J`, `H`, `O`), the three jump profiles (`SMART`, `COMPETITIVE`, `STABLE`), HUD actionbar feedback, and in-game settings. |
| **[Configuration](Configuration.md)** | Explains `.minecraft/config/jumpdelayfix.properties`, per-server LRU profile persistence, and auto profile switching. |
| **[Installation & Compatibility](Installation-and-Compatibility.md)** | Supported Minecraft versions (1.21.9–1.21.11, 26.1.x, 26.2.x, 26.3), Java runtime requirements (Java 21 vs 25), and installation guides. |
| **[Anti-Cheat & Fair Play](Anti-Cheat-and-Fair-Play.md)** | Deep dive into how client-side jump input simulation works, server physics compliance, and compatibility with GrimAC, Vulcan, Polar, Matrix, Intave, and NCP. |
| **[Troubleshooting & FAQ](Troubleshooting.md)** | Solutions for rubberbanding, keybinding conflicts, Java version errors, and configuration resets. |
| **[Development & Release](Development-and-Release.md)** | Architecture overview, multi-branch workflow, Gradle build commands, strict test gates, and automated release pipeline. |

---

## ⚡ Quick Reference

### In-Game Keybinds (Defaults)

| Key | Action | Details |
| :---: | :--- | :--- |
| **`J`** | **Toggle Mod** | Instantly turns Jump Delay Fix ON or OFF. |
| **`H`** | **Cycle Profile** | Switches between `SMART` → `COMPETITIVE` → `STABLE`. |
| **`O`** | **Open Settings** | Directly opens the in-game configuration screen. |

> *All keybinds are fully rebindable in Minecraft's **Options → Controls → Key Binds** under the **Jump Delay Fix** category.*

### Jump Profiles Overview

- **`SMART`** *(Default)*: Dynamically adjusts jump input timing based on server ping and rollback rate. Perfect for everyday survival and general multiplayer servers.
- **`COMPETITIVE`**: -1 tick ground latency gate. Designed for low-latency PvP (Bedwars, Duels, Parkour) when server connection is fast (< 95ms ping).
- **`STABLE`**: +1 tick conservative timing. Designed for high-latency connections (> 210ms ping) or servers with strict anti-cheat rollback policies.

---

## 🌐 Project Links

- **Source Code:** [GitHub Repository](https://github.com/kizio806/jump-delay-fix)
- **Mod Releases:** [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases)
- **Modrinth Project:** [Modrinth Page](https://modrinth.com/mod/jump-delay-fix)
- **Issue Tracker:** [GitHub Issues](https://github.com/kizio806/jump-delay-fix/issues)
