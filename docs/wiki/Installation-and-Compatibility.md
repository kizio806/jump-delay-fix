# Installation and Compatibility Guide

This guide provides full instructions for installing Jump Delay Fix on Fabric and NeoForge for Minecraft **26.2**.

---

## 📦 Compatibility & Version Matrix

| Minecraft Version | Release Line | Mod Version | Supported Loaders | Loader Requirements | Java Runtime |
| :--- | :---: | :---: | :--- | :--- | :---: |
| **26.2** | `support/26.2.x` | **`1.3.2`** | **Fabric**<br>**NeoForge** | Fabric Loader >= 0.18.4 (Fabric API 0.159.0+)<br>NeoForge >= 26.2.0.79 | **Java 25** |

> 🔗 For the complete technical compatibility matrix and artifact checksums, see [docs/compatibility-matrix.md](https://github.com/kizio806/jump-delay-fix/blob/main/docs/compatibility-matrix.md).

---

## ☕ Java Requirements

- **Minecraft 26.2:** Requires **Java 25** (JDK/JRE 25). Modern Minecraft launchers (Official Launcher, Prism Launcher, Modrinth App) bundle this automatically when creating a 26.2 instance.

---

## 📥 Installation Steps

### Option A: Fabric

1. **Install Fabric Loader:** Run the official installer from [fabricmc.net](https://fabricmc.net/) for Minecraft 26.2.
2. **Install Fabric API:** Download the matching [Fabric API](https://modrinth.com/mod/fabric-api) version (0.159.0+26.2) and place it in your `.minecraft/mods` folder.
3. *(Recommended)* **Install ModMenu:** Download [ModMenu](https://modrinth.com/mod/modmenu) to access the settings GUI directly from Minecraft's mod menu.
4. **Download Jump Delay Fix:**
   - Grab `jumpdelayfix-fabric-26.2-1.3.2.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
5. **Place into Mods Folder:** Move the JAR into `.minecraft/mods`.
6. **Launch Game:** Start Minecraft using your Fabric profile.

---

### Option B: NeoForge

1. **Install NeoForge:** Run the official installer from [neoforged.net](https://neoforged.net/) for Minecraft 26.2 (version 26.2.0.79 or newer).
2. **Download Jump Delay Fix:**
   - Grab `jumpdelayfix-neoforge-26.2-1.3.2.jar` from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases).
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
