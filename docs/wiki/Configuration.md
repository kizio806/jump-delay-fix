# Configuration Guide

Jump Delay Fix provides a lightweight, resilient configuration system designed to persist your preferences without causing disk micro-stutters or frame drops.

---

## 📁 File Location & Format

Configuration is stored in standard Java Properties format (`key=value`) at:

```
.minecraft/config/jumpdelayfix.properties
```

*(On custom launchers like Prism Launcher, Modrinth App, or CurseForge, this file is located inside your profile's `config/` directory.)*

---

## ⚙️ Configuration Properties

Here is an example of a generated `jumpdelayfix.properties` file:

```properties
autoProfileSwitch=true
serverProfile.bG9jYWxob3N0OjI1NTY1=COMPETITIVE
serverProfile.bXkuc2VydmVyLm5ldA===STABLE
```

### Property Breakdown

| Key | Value Type | Default | Description |
| :--- | :---: | :---: | :--- |
| `autoProfileSwitch` | `boolean` (`true` / `false`) | `true` | When `true`, dynamically switches profiles based on network latency and rollback telemetry. When `false`, the profile stays fixed on your manual selection. |
| `serverProfile.<encoded>` | `String` (`SMART`, `COMPETITIVE`, `STABLE`) | `SMART` | Remembers your chosen jump profile for a specific server. The key suffix is the Base64-encoded server address (or `global` for singleplayer/fallback). |

---

## 💾 Per-Server Memory & LRU Caching

Jump Delay Fix automatically recognizes which server or world you are playing on and loads your previously selected profile:

- **Server Identifier:** Uses the server's network address (e.g. `mc.hypixel.net`, `play.example.com:25565`) or `global` for singleplayer worlds.
- **Base64 Encoding:** Server addresses are encoded to ensure valid property keys without whitespace, colons, or punctuation issues.
- **LRU Eviction (Least Recently Used):** The mod caches up to **128 servers**. When you connect to a 129th server, the oldest inactive server entry is automatically pruned to keep the configuration file clean and lightweight.

---

## 🚀 Performance & Thread Safety

- **Asynchronous I/O:** Disk writes are dispatched to a dedicated daemon thread (`jumpdelayfix-config-io`), ensuring that saving settings never freezes the main Minecraft render thread.
- **Debounced Writes:** Rapid in-game adjustments (such as cycling profiles several times) are debounced by 10 game ticks to avoid redundant file operations.
- **Atomic Operations:** Uses atomic file replacement (or safe temporary file fallback) to prevent file corruption in case of unexpected system power loss or crashes.
- **Shutdown & Disconnect Flush:** A registered JVM shutdown hook and world disconnect listener ensure that any pending configuration changes are saved before the game closes.

---

## 🔄 Resetting Configuration

### Method 1: In-Game Settings Screen
Open the settings screen (`O` key by default, or via ModMenu / Mod List) and click the **Reset to Defaults** button (`Przywróć domyślne`).

### Method 2: Manual File Deletion
1. Close Minecraft.
2. Navigate to your `.minecraft/config/` directory.
3. Delete `jumpdelayfix.properties`.
4. Launch Minecraft. The file will be regenerated with factory defaults on first save.
