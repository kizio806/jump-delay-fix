# Troubleshooting & FAQ

---

## 🛠️ Common Issues and Solutions

### 1. Jumping feels delayed or rubberbanding occurs on multiplayer servers
- **Cause:** High network latency (ping > 150 ms), server tick drops, or strict movement validation.
- **Solution:** Press the cycle profile key (**`H`** by default) to switch to the **`STABLE`** profile, or enable **Auto Profile Switching** in the settings screen (**`O`** by default). The mod will automatically adapt grounded timing to match your connection speed.

---

### 2. Keybindings conflict with other mods
- **Default Keybinds:**
  - **`J`** — Toggle Jump Delay Fix (ON / OFF)
  - **`H`** — Cycle Profile (`SMART` → `STABLE` → `COMPETITIVE`)
  - **`O`** — Open Settings GUI directly
- **Solution:** Open Minecraft Options → Controls → Key Binds, scroll down to the **Jump Delay Fix** category, and reassign conflicting keys.

---

### 3. Resetting Configuration
- **In-Game:** Open the settings screen (**`O`** by default or via ModMenu / NeoForge Mod List) and click **Reset to Defaults**.
- **Manual:** Delete `.minecraft/config/jumpdelayfix.properties` while the game is closed.

---

### 4. Game crash on launch (`UnsupportedClassVersionError` or `ClassNotFoundException`)
- **Check Java Version:**
  - Minecraft 26.x requires **Java 25**.
  - Minecraft 1.21.x requires **Java 21**.
- **Check Loader Compatibility:**
  - Fabric requires Fabric Loader >= 0.18.4 and compatible Fabric API.
  - NeoForge requires compatible NeoForge version.
- Ensure you placed only one loader build of Jump Delay Fix in your `mods/` directory.

---

### 5. Need more help?
- File an issue on GitHub: [https://github.com/kizio806/jump-delay-fix/issues](https://github.com/kizio806/jump-delay-fix/issues)
