# ⚡ Jump Delay Fix

[![CI Status](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml/badge.svg)](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml)
[![SonarCloud Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=kizio806_jump-delay-fix&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kizio806_jump-delay-fix)
[![Codecov Coverage](https://codecov.io/gh/kizio806/jump-delay-fix/graph/badge.svg)](https://codecov.io/gh/kizio806/jump-delay-fix)
[![GitHub Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver&logo=github)](https://github.com/kizio806/jump-delay-fix/releases)
[![Modrinth](https://img.shields.io/badge/Modrinth-Jump%20Delay%20Fix-00AF5C?logo=modrinth)](https://modrinth.com/mod/jump-delay-fix)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix)](LICENSE)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-blue?logo=fabric)](https://fabricmc.net/)
[![NeoForge](https://img.shields.io/badge/Loader-NeoForge-orange?logo=neoforge)](https://neoforged.net/)

**Jump Delay Fix** is a lightweight, **100% client-side** Minecraft mod designed to eliminate the artificial jump delay for smoother, instant, and hyper-responsive movement. Built for casual exploration, parkour precision, and competitive PvP!

---

## 📖 Table of Contents

- [Features](#features)
- [Jump Profiles](#jump-profiles)
- [Controls & Hotkeys](#controls--hotkeys)
- [Multiplayer & Anti-Cheat Compatibility](#multiplayer--anti-cheat-compatibility)
- [Supported Versions & Platforms](#supported-versions--platforms)
- [Installation](#installation)
- [Configuration](#configuration)
- [Building from Source](#building-from-source)
- [Wiki & Documentation](#wiki--documentation)
- [License](#license)

---

## ✨ Features

- 🚀 **Instant Jump Response:** In vanilla Minecraft, holding the jump key imposes a hardcoded delay (up to 10 ticks) between landing and jumping again. Jump Delay Fix eliminates this artificial pause, executing jumps immediately upon ground contact.
- ⚡ **Multi-Loader Support:** Seamless compatibility with both **Fabric** and **NeoForge**.
- 🧠 **Smart Adaptive Tuning:** Continuously monitors server latency and rollback history. If connection lag or server checks are detected, the mod automatically transitions to safer timing parameters to prevent rubberbanding.
- 💾 **Per-Server Profile Memory:** Remembers your preferred jump profile for each multiplayer server or singleplayer world you visit (persisting up to 128 servers in `.minecraft/config/jumpdelayfix.properties`).
- ⚙️ **In-Game Settings GUI:** Integrated directly into **ModMenu** (Fabric) and the **Mod Options** screen (NeoForge), or opened via hotkey (`O`), complete with a one-click **"Reset to Defaults"** action.
- 💬 **Actionbar HUD Notifications:** Clear, non-intrusive actionbar alerts when toggling the mod or switching profiles, fully localized across 10 languages (English, Polish, German, Spanish, French, Italian, Japanese, Portuguese, Russian, Chinese).
- 🛡️ **100% Client-Side & Fair Play:** No server installation required. Does not modify player velocity, fly packets, or bypass vanilla jump heights—it strictly optimizes client-side jump input timing.

---

## 🎮 Jump Profiles

Jump Delay Fix offers three calibrated operational profiles:

| Profile | Ground Latency Gate | Target Network Latency | Recommended For |
| :--- | :---: | :---: | :--- |
| **`SMART`** *(Default)* | Dynamic (0 ticks baseline) | Standard (0 – 200 ms) | **Everyday play & general servers.** Automatically adapts timing based on rollback rate and ping. |
| **`COMPETITIVE`** | -1 tick (Ultra-low latency) | Ping < 95 ms, rollback < 8% | **PvP, Bedwars, parkour & low-ping servers.** Fires jump inputs as early as ground contact is verified. |
| **`STABLE`** | +1 tick (Anti-rubberband) | Ping > 210 ms, rollback > 30% | **High-ping connections & strict anti-cheat servers.** Adds conservative ground verification to mitigate lagbacks. |

> 💡 **Auto Profile Switching:** When enabled, the mod dynamically steps between `COMPETITIVE`, `SMART`, and `STABLE` depending on real-time server conditions. You can toggle this feature at any time in the settings menu.

---

## ⌨️ Controls & Hotkeys

All keybindings can be customized in Minecraft's **Options → Controls → Key Binds** under the **Jump Delay Fix** category:

| Action | Default Key | Translation Key | Description |
| :--- | :---: | :--- | :--- |
| **Toggle Mod** | `J` | `key.jumpdelayfix.toggle` | Instantly enables or disables Jump Delay Fix in-game. |
| **Cycle Profile** | `H` | `key.jumpdelayfix.profile` | Cycles sequentially: `SMART` → `STABLE` → `COMPETITIVE`. |
| **Open Settings** | `O` | `key.jumpdelayfix.config` | Opens the Jump Delay Fix configuration screen directly. |

---

## 🛡️ Multiplayer & Anti-Cheat Compatibility

Jump Delay Fix is engineered with strict adherence to fair-play and server protocol safety:

- **No Spoofed Movement:** The mod never sends impossible movement packets, alters jump trajectory, or grants illegitimate vertical velocity.
- **Client Input Simulation:** It executes the standard vanilla `player.jumpFromGround()` routine exactly when the client establishes valid ground contact.
- **Anti-Cheat Compatibility:** Tested with common server anti-cheat solutions, including **GrimAC**, **Vulcan**, **Polar**, **Matrix**, **Intave**, and **NCP** (using adaptive profiles for varying latency).
- **Self-Healing Fallback:** If a server experiences sudden tick-rate drops or network jitter, the mod's built-in rollback watcher automatically backs off into `STABLE` profile to eliminate rubberbanding.

---

## 📦 Supported Versions & Platforms

We maintain dedicated branches and published releases for every major modern Minecraft release:

| Minecraft Line | Version Tag | Supported Game Versions | Mod Loaders | Java Baseline | Git Branch |
| :--- | :---: | :--- | :--- | :---: | :--- |
| **26.3** *(Latest)* | `v1.4.2` | **26.3** | Fabric 0.18.4+ (Fabric API 0.160.6+)<br>NeoForge 26.3.0.1-beta+ | **Java 25** | [`main`](https://github.com/kizio806/jump-delay-fix) |
| **26.2** | `v1.3.2` | **26.2** | Fabric 0.18.4+ (Fabric API 0.159.0+)<br>NeoForge 26.2.0.79+ | **Java 25** | [`support/26.2.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.2.x) |
| **26.1** | `v1.2.2` | **26.1**, **26.1.1**, **26.1.2** | Fabric 0.18.4+ (Fabric API 0.155.2+)<br>NeoForge 26.1.2.103+ | **Java 25** | [`support/26.1.x`](https://github.com/kizio806/jump-delay-fix/tree/support/26.1.x) |
| **1.21** | `v1.1.2` | **1.21.9**, **1.21.10**, **1.21.11** | Fabric 0.18.4+ (Fabric API 0.134.1+ / 0.138.4+ / 0.141.6+)<br>NeoForge 21.9.16+, 21.10.64+, 21.11.48+ | **Java 21** | [`support/1.21.9-1.21.11`](https://github.com/kizio806/jump-delay-fix/tree/support/1.21.9-1.21.11) |

> 📖 **Full Compatibility & Checksums Matrix:** See [docs/compatibility-matrix.md](docs/compatibility-matrix.md) for detailed verification results across all 16 loader-Minecraft combinations.

---

## 📥 Installation

### Fabric
1. Install [Fabric Loader](https://fabricmc.net/) for your target Minecraft version.
2. Download and place the matching [Fabric API](https://modrinth.com/mod/fabric-api) into your `.minecraft/mods` folder.
3. *(Optional)* Install [ModMenu](https://modrinth.com/mod/modmenu) to access the settings GUI from the mods list.
4. Download `jumpdelayfix-fabric-<mc>-<version>.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
5. Place the JAR into `.minecraft/mods` and launch Minecraft.

### NeoForge
1. Install [NeoForge](https://neoforged.net/) for your target Minecraft version.
2. Download `jumpdelayfix-neoforge-<mc>-<version>.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
3. Place the JAR into `.minecraft/mods` and launch Minecraft.

---

## ⚙️ Configuration

Settings are saved in `.minecraft/config/jumpdelayfix.properties`:

```properties
autoProfileSwitch=true
serverProfile.bWluZWNyYWZ0LmV4YW1wbGUuY29t=SMART
```

- **`autoProfileSwitch`:** Controls whether the mod can automatically change profiles based on connection telemetry (`true` / `false`).
- **`serverProfile.<base64ServerId>`:** Stores the saved profile (`SMART`, `COMPETITIVE`, `STABLE`) for each visited server.
- The configuration uses atomic file operations and asynchronous background I/O with debounce to eliminate disk stutter.

---

## 🛠️ Building from Source

### Prerequisites
- JDK 25 (for `main`, `support/26.2.x`, `support/26.1.x`) or JDK 21 (for `support/1.21.9-1.21.11`)
- Git

### Build Commands
```bash
# Clone the repository
git clone https://github.com/kizio806/jump-delay-fix.git
cd jump-delay-fix

# Compile and package release JARs for all loaders
./gradlew --no-daemon clean buildAll

# Run test suite & code contracts
./gradlew --no-daemon test

# Run strict release qualification gates
./gradlew --no-daemon strictCheck publishReadyCheck
```

---

## 📚 Wiki & Documentation

For in-depth guides and developer documentation, visit our wiki and reference docs:

- **[GitHub Wiki](https://github.com/kizio806/jump-delay-fix/wiki)**
  - [Wiki Home](docs/wiki/Home.md)
  - [User Guide](docs/wiki/User-Guide.md)
  - [Configuration Guide](docs/wiki/Configuration.md)
  - [Installation & Compatibility Matrix](docs/wiki/Installation-and-Compatibility.md)
  - [Anti-Cheat & Fair Play Guide](docs/wiki/Anti-Cheat-and-Fair-Play.md)
  - [Development & Release Guide](docs/wiki/Development-and-Release.md)
  - [Troubleshooting & FAQ](docs/wiki/Troubleshooting.md)
- **Technical Documentation:**
  - [Architecture Assessment](docs/architecture.md)
  - [Development Guidelines](docs/development.md)
  - [Installation Guide](docs/installation.md)
  - [Branch Compatibility Matrix](docs/compatibility-matrix.md)
  - [Releases Guide](docs/releases.md)
  - [Modrinth Publishing Guide](docs/modrinth.md)
  - [Troubleshooting & FAQ](docs/troubleshooting.md)
  - [Changelog](CHANGELOG.md)

---

## 📄 License

Jump Delay Fix is licensed under the [Apache License 2.0](LICENSE).
