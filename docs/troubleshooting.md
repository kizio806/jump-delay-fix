# Troubleshooting & FAQ

## Common Issues and Solutions

### 1. Jumping feels delayed or rubberbanding occurs on multiplayer servers
- **Cause:** High network latency (ping > 150 ms) or strict server movement validation.
- **Solution:** Press the cycle profile key (**`H`** by default) to switch to the **`STABLE`** profile, or enable **Auto Profile Switching** in the settings screen (**`O`** by default). The mod will automatically adapt grounded timing to match your connection speed.

### 2. Keybindings conflict with other mods
- **Default Keybinds:**
  - **`J`** — Toggle Jump Delay Fix (ON / OFF)
  - **`H`** — Cycle Profile (`SMART` → `STABLE` → `COMPETITIVE`)
  - **`O`** — Open Settings GUI
- **Solution:** Open Minecraft Options → Controls → Key Binds, scroll down to the **Jump Delay Fix** category, and reassign conflicting keys.

### 3. Resetting Configuration
- **Solution:** Open the settings screen (**`O`** by default or via ModMenu / NeoForge Mod List) and click **Reset to Defaults**. Alternatively, delete `.minecraft/config/jumpdelayfix.properties` while the game is closed.

### 4. Game crash on launch (ClassNotFound or Java version error)
- **Check Java Version:** Minecraft 26.x requires **Java 25**. Ensure your launcher uses Java 25.
- **Check Loader Compatibility:**
  - Fabric requires Fabric Loader >= 0.18.4 and compatible Fabric API for 26.1.x.
  - NeoForge requires NeoForge >= 26.1.2.103.
- Ensure you placed only one loader build of Jump Delay Fix in your `mods/` directory.

### 5. Need more help?
- File an issue on GitHub: [https://github.com/kizio806/jump-delay-fix/issues](https://github.com/kizio806/jump-delay-fix/issues)
