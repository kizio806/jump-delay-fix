# Troubleshooting & FAQ

This guide provides solutions to common issues, configuration problems, and error messages when running Jump Delay Fix.

---

## 🔍 Frequently Encountered Issues

### 1. I am experiencing rubberbanding or lagbacks on multiplayer servers
- **Cause:** Network latency spikes (ping > 150 ms), severe server tick drops, or a strict server anti-cheat misinterpreting timing under packet loss.
- **Solution:**
  1. Press **`H`** to switch your profile to **`STABLE`**. The `STABLE` profile introduces an extra grounded verification tick to guarantee synchronization with the server.
  2. Open settings (**`O`**) and ensure **Auto Profile Switching** is enabled. The mod will automatically adapt to network lag.
  3. If the server enforces zero-modification policies, toggle the mod off with **`J`**.

---

### 2. Keybinds conflict with another mod
- **Default Keybinds:**
  - **`J`** — Toggle Mod ON / OFF (`key.jumpdelayfix.toggle`)
  - **`H`** — Cycle Profile (`key.jumpdelayfix.profile`)
  - **`O`** — Open Settings Screen (`key.jumpdelayfix.config`)
- **Solution:**
  1. Go to **Options → Controls → Key Binds**.
  2. Scroll to the **Jump Delay Fix** category.
  3. Reassign the conflicting keys or press `ESC` to unbind them.

---

### 3. How do I completely reset all settings?
- **In-Game Method:**
  1. Open the settings screen by pressing **`O`** (or through ModMenu / NeoForge Mod List).
  2. Click **Reset to Defaults** (`Przywróć domyślne`).
  3. All cached server profiles and runtime settings will be restored to factory defaults immediately.
- **Manual Method:**
  1. Close Minecraft.
  2. Navigate to your `.minecraft/config/` folder.
  3. Delete `jumpdelayfix.properties`.
  4. Restart Minecraft.

---

### 4. Game crashes on launch with `UnsupportedClassVersionError` or `ClassNotFoundException`
- **Cause:** Incompatible Java runtime version or mismatched mod loader.
- **Solutions:**
  - **Check Java Version:**
    - Minecraft **26.x** requires **Java 25**.
    - Minecraft **1.21.x** requires **Java 21**.
    - Make sure your launcher instance is configured to use the appropriate JDK.
  - **Check Loader Compatibility:**
    - If you are running **Fabric**, make sure you installed `jumpdelayfix-fabric-...jar` and have [Fabric API](https://modrinth.com/mod/fabric-api) installed.
    - If you are running **NeoForge**, make sure you installed `jumpdelayfix-neoforge-...jar`.
    - Do NOT place both Fabric and NeoForge JARs in the same `mods/` directory.

---

### 5. Settings screen doesn't open from the Mods list
- **On Fabric:** Fabric does not have a built-in mods GUI menu. You need to install [ModMenu](https://modrinth.com/mod/modmenu) to access the settings button in the mods list, or simply press the **`O`** key in-game.
- **On NeoForge:** Open **Mods → Jump Delay Fix → Config**, or press **`O`** in-game.

---

### 6. Where can I find diagnostic logs?
Jump Delay Fix logs initialization and configuration events with the `jumpdelayfix` tag:
1. Open `.minecraft/logs/latest.log`.
2. Search for `Jump Delay Fix`.
3. You should see log lines like:
   ```
   [Client thread/INFO] [jumpdelayfix]: Jump Delay Fix Fabric client starting
   [Client thread/INFO] [jumpdelayfix]: Jump Delay Fix initialized
   ```

---

## 💬 Getting Further Help

If you encounter an issue not covered here:
- Open an issue on our [GitHub Issue Tracker](https://github.com/kizio806/jump-delay-fix/issues).
- Include your Minecraft version, mod loader version, Jump Delay Fix version, and relevant excerpts from `.minecraft/logs/latest.log`.
