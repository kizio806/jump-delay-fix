# Troubleshooting Guide

This guide covers common questions and resolution steps when using Jump Delay Fix.

---

## 1. Mod Does Not Load or Game Crashes on Startup

- **Verify Java Version:** Ensure your Minecraft launcher uses Java 21 or newer. Java 17 or older will fail to load mods compiled for 1.21.x.
- **Fabric API Missing:** If playing on Fabric, verify that `fabric-api` is present in your `.minecraft/mods` folder. Jump Delay Fix requires Fabric API on Fabric.
- **Mod Loader Mismatch:** Verify you are running the correct JAR for your loader:
  - Fabric: `jumpdelayfix-fabric-...jar`
  - NeoForge: `jumpdelayfix-neoforge-...jar`
- **Duplicate JARs:** Remove any older versions of Jump Delay Fix from your `mods/` folder.

---

## 2. Rubberbanding or Setbacks on Multiplayer Servers

When playing on servers with strict movement checks or high network latency:
- **Enable Auto Profile Switching:** Open settings (**O**) and ensure **Auto Profile** is enabled (`ON`). The mod will monitor latency and rollbacks and adapt timing automatically.
- **Switch to Stable Profile:** Press **H** to switch to the **Stable Anti-Rubberband** profile. This profile adds conservative grounding ticks before jumps, preventing rejected movement packets.
- **Avoid Competitive Profile on Laggy Connections:** The **Competitive** profile minimizes delay for low-ping environments (< 95ms). Using it on unstable or high-ping connections (> 150ms) can cause server desynchronization.

---

## 3. Keybinds Not Working or Conflicting

- Default keybindings:
  - **J** — Toggle mod ON / OFF
  - **H** — Cycle active Jump Profile
  - **O** — Open Jump Delay Fix Settings GUI
- If these keys conflict with other mods or controls, navigate to:
  `Options` -> `Controls` -> `Key Binds` -> `Jump Delay Fix` category, and reassign them as desired.

---

## 4. Resetting Configuration

If settings become desynchronized or you want to start fresh:
- Open the settings screen in-game (**O**) and click **Reset to Defaults**.
- Alternatively, while Minecraft is closed, delete `config/jumpdelayfix.properties` in your `.minecraft` directory. It will be recreated with default values on next launch.

---

## 5. Server Anti-Cheat & Fair Play Notice

> [!WARNING]
> Jump Delay Fix is a client-side utility mod. It removes client-side input delays and optimizes jump initiation timing. It does not provide unfair vertical reach, speed modifications, or packet manipulation. However, server rules vary widely. Always ensure client-side movement enhancements are permitted on the servers where you play.
