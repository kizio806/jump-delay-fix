# Installation and Compatibility Guide

This guide provides full instructions for installing Jump Delay Fix on Fabric and NeoForge across Minecraft **1.21.x** (`1.21.9`, `1.21.10`, `1.21.11`).

---

## 📦 Compatibility & Version Matrix

Jump Delay Fix provides dedicated versioned artifacts for each supported Minecraft 1.21 patch release across both loaders:

| Minecraft Version | Release Line | Mod Version | Supported Loaders | Loader Requirements | Java Runtime |
| :--- | :---: | :---: | :--- | :--- | :---: |
| **1.21.9** | `support/1.21.9-1.21.11` | **`1.1.2`** | **Fabric**<br>**NeoForge** | Fabric Loader >= 0.18.4 (Fabric API `0.134.1+1.21.9`)<br>NeoForge >= 21.9.16-beta | **Java 21** |
| **1.21.10** | `support/1.21.9-1.21.11` | **`1.1.2`** | **Fabric**<br>**NeoForge** | Fabric Loader >= 0.18.4 (Fabric API `0.138.4+1.21.10`)<br>NeoForge >= 21.10.64 | **Java 21** |
| **1.21.11** | `support/1.21.9-1.21.11` | **`1.1.2`** | **Fabric**<br>**NeoForge** | Fabric Loader >= 0.18.4 (Fabric API `0.141.6+1.21.11`)<br>NeoForge >= 21.11.48 | **Java 21** |

> 🔗 For the complete technical compatibility matrix and artifact checksums, see [docs/compatibility-matrix.md](https://github.com/kizio806/jump-delay-fix/blob/main/docs/compatibility-matrix.md).

---

## ☕ Java Requirements

- **Minecraft 1.21.x:** Requires **Java 21** (JDK/JRE 21). Official and modern Minecraft launchers (Official Launcher, Prism Launcher, Modrinth App) bundle Java 21 automatically when creating a 1.21.x instance.

---

## 📥 Installation Steps

### Option A: Fabric

1. **Install Fabric Loader:** Run the official installer from [fabricmc.net](https://fabricmc.net/) for your specific Minecraft version (`1.21.9`, `1.21.10`, or `1.21.11`).
2. **Install Fabric API:** Download the matching [Fabric API](https://modrinth.com/mod/fabric-api) version and place it in your `.minecraft/mods` folder:
   - For MC 1.21.9: `fabric-api-0.134.1+1.21.9.jar`
   - For MC 1.21.10: `fabric-api-0.138.4+1.21.10.jar`
   - For MC 1.21.11: `fabric-api-0.141.6+1.21.11.jar`
3. *(Recommended)* **Install ModMenu:** Download [ModMenu](https://modrinth.com/mod/modmenu) to access the settings GUI directly from Minecraft's mod menu.
4. **Download Jump Delay Fix:**
   - Grab the matching versioned JAR (`jumpdelayfix-fabric-<mc_version>-1.1.2.jar`) from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
5. **Place into Mods Folder:** Move the JAR into `.minecraft/mods`.
6. **Launch Game:** Start Minecraft using your Fabric profile.

---

### Option B: NeoForge

1. **Install NeoForge:** Run the official installer from [neoforged.net](https://neoforged.net/) for your target Minecraft version:
   - For MC 1.21.9: NeoForge `21.9.16-beta` or newer
   - For MC 1.21.10: NeoForge `21.10.64` or newer
   - For MC 1.21.11: NeoForge `21.11.48` or newer
2. **Download Jump Delay Fix:**
   - Grab `jumpdelayfix-neoforge-<mc_version>-1.1.2.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
3. **Place into Mods Folder:** Move the JAR into `.minecraft/mods`.
4. **Launch Game:** Start Minecraft using your NeoForge profile. Settings can be accessed via the **Mods** menu or in-game hotkey (`O`).

---

## 💻 Launcher-Specific Instructions

### Prism Launcher
1. Right-click your Minecraft instance and choose **Edit Instance**.
2. Click **Mods** in the left sidebar.
3. Click **Add Mod** (or drag and drop the downloaded `.jar`).
4. Ensure the required Fabric API / NeoForge is checked in the **Version** tab.

### Modrinth App
1. Open your profile in the Modrinth App.
2. Click **Add Content**.
3. Search for **Jump Delay Fix** and click **Install**.

### Official Minecraft Launcher
1. Open the Minecraft Launcher and click the **Installations** tab.
2. Hover over your Fabric or NeoForge profile and click the folder icon to open `.minecraft`.
3. Open the `mods` folder and place the downloaded JAR file inside.

---

## 🌐 Server-Side Status

- **Jump Delay Fix is 100% Client-Side.**
- **Do NOT install on dedicated servers:** The mod has no server-side component. Servers run standard vanilla, Paper, Purpur, or modded engines without requiring Jump Delay Fix.
- Players with Jump Delay Fix installed on their client can connect to any vanilla or modded server freely.
