# User Guide

This guide details everything you need to know about using **Jump Delay Fix** in Minecraft, including default controls, jump profiles, the settings interface, and HUD feedback.

---

## ⌨️ In-Game Controls & Keybindings

Jump Delay Fix registers dedicated key mappings in Minecraft's standard controls system.

| Action | Default Key | Translation Key | Description |
| :--- | :---: | :--- | :--- |
| **Toggle Mod** | **`J`** | `key.jumpdelayfix.toggle` | Instantly toggles the mod ON or OFF in-game. |
| **Cycle Profile** | **`H`** | `key.jumpdelayfix.profile` | Cycles sequentially through `SMART` → `STABLE` → `COMPETITIVE`. |
| **Open Settings** | **`O`** | `key.jumpdelayfix.config` | Opens the graphical configuration interface directly. |

### Customizing Keybinds
To customize any of these keys:
1. Open Minecraft **Options → Controls → Key Binds**.
2. Scroll to the **Jump Delay Fix** category.
3. Click any keybind and press your preferred key, or press `ESC` to unbind.

---

## 🎮 The Three Jump Profiles

Jump Delay Fix does not simply eliminate jump cooldown—it allows you to tailor ground contact verification to your playstyle and server environment.

### 1. `SMART` *(Default & Recommended)*
- **Baseline Offset:** 0 ticks
- **Target Ping Range:** 0 – 200 ms
- **Description:** An intelligent adaptive mode that provides immediate responsiveness while safeguarding against server-side lagbacks. The mod continuously tracks server ping and recent rollback attempts. If the server connection degrades, it seamlessly falls back to conservative timing; if conditions improve, it restores fast jumping.
- **Recommended For:** Everyday survival, general multiplayer servers, realm servers, and minigame lobbies.

### 2. `COMPETITIVE`
- **Baseline Offset:** -1 tick (Ultra-low ground latency)
- **Target Ping Range:** < 95 ms with low rollback rate (< 8%)
- **Description:** Designed for high-speed PvP scenarios where every fraction of a second matters. The mod initiates jumping at the very earliest frame of ground verification.
- **Recommended For:** Bedwars, Skywars, PvP duels, precision parkour, and low-latency servers.
- *Note:* If server ping spikes while using this mode with auto-switch disabled, you may experience minor rubberbanding if the server's tick rate lags behind.

### 3. `STABLE`
- **Baseline Offset:** +1 tick (Anti-rubberband safeguard)
- **Target Ping Range:** > 210 ms or servers with high rollback rate (> 30%)
- **Description:** Adds an extra ground contact verification tick before executing the jump. This ensures servers with strict movement checks, high latency, or heavy packet loss never flag your jumps or pull you back.
- **Recommended For:** High-ping multiplayer (e.g., cross-region play), laggy servers, or servers running hyper-strict anti-cheat configurations.

---

## 🧠 Smart Auto Profile Switching

When **Auto Profile Switching** is enabled (default: `true`), Jump Delay Fix monitors connection telemetry in the background:

- **Confirmed vs Rejected Jumps:** The mod keeps track of completed jumps versus rollbacks (sudden unexpected Y-coordinate resets immediately after jumping).
- **Latency Monitoring:** Network round-trip latency (ping) is evaluated every tick.
- **Dynamic Transition:**
  - If ping is `< 95 ms` and rollback rate is `< 8%` over recent jumps, the mod promotes your profile to `COMPETITIVE`.
  - If ping exceeds `210 ms` or rollback rate surpasses `30%`, the mod automatically steps down to `STABLE` to prevent rubberbanding.
  - If conditions normalize, it returns to `SMART`.
- You can disable auto profile switching in the settings screen if you prefer strict manual control.

---

## ⚙️ In-Game Settings Screen

You can access the settings screen in three ways:
1. Press the **`O`** key in-game (default keybind).
2. On **Fabric**: via **Mods → Jump Delay Fix → Configure** (requires [ModMenu](https://modrinth.com/mod/modmenu)).
3. On **NeoForge**: via **Mods → Jump Delay Fix → Config** directly from the built-in mod list.

### Available Options:
- **Mod State:** Toggle the entire mod `ON` or `OFF`.
- **Auto Profile Switching:** Toggle adaptive switching `ON` or `OFF`.
- **Active Profile:** Cycle manual profile between `SMART`, `COMPETITIVE`, and `STABLE`.
- **Reset to Defaults:** Restores factory configuration with a single click:
  - Enables the mod.
  - Sets active profile to `SMART`.
  - Enables auto profile switching.
  - Clears all cached per-server profiles.
  - Clears transient jump and rollback counters.

---

## 💬 Actionbar HUD Feedback

Whenever you toggle the mod with `J` or cycle profiles with `H`, Jump Delay Fix displays a non-intrusive actionbar notification above your hotbar:

- Example: `Jump Delay Fix: ON` / `Jump Delay Fix: OFF`
- Example: `Jump Profile: SMART` / `Jump Profile: COMPETITIVE` / `Jump Profile: STABLE`

Actionbar messages are fully localized in:
- English (`en_us`)
- Polish (`pl_pl`)
- German (`de_de`)
- Spanish (`es_es`)
- French (`fr_fr`)
- Italian (`it_it`)
- Japanese (`ja_jp`)
- Portuguese (`pt_br`)
- Russian (`ru_ru`)
- Chinese (`zh_cn`)

---

## 🛡️ Player Lifecycle Safety

Jump Delay Fix includes robust lifecycle guards to prevent accidental or buggy jumps:
- **Dead Players:** Jumps are completely suppressed while dead or on the respawn screen (`player.isAlive()`).
- **Sleeping Players:** Jump inputs are ignored while in a bed (`!player.isSleeping()`).
- **Entity Transitions:** Player entity ID changes (such as respawning, world changes, or server dimension swaps) reset transient state to avoid carrying over ground momentum.
