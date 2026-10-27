# Anti-Cheat & Fair Play Guide

This document explains the technical mechanics of **Jump Delay Fix**, how it interacts with Minecraft server physics engines, and its compatibility with popular anti-cheat solutions.

---

## 🔬 How Jump Delay Fix Operates

### The Vanilla Mechanism
In vanilla Minecraft, the client player entity (`LocalPlayer`) contains an internal jump cooldown timer (`jumpDelay`, default: 10 game ticks). When holding down the jump key (spacebar), Minecraft forces the player to wait for this cooldown timer to tick down to zero after landing before allowing another jump.

This causes a distinct, artificial hesitation between landing and jumping, often disrupting continuous sprinting, parkour flow, and PvP combo chaining.

### What Jump Delay Fix Changes
Jump Delay Fix intercepts this delay logic purely on the client side:
1. It verifies that the player is actively holding down the jump key.
2. It verifies that the player is validly touching the ground (`player.onGround()`).
3. It performs safety checks: the player must be alive (`player.isAlive()`), not sleeping in a bed (`!player.isSleeping()`), and not in a respawn transition.
4. When grounded contact is confirmed according to the active profile's latency gate, it invokes standard vanilla `player.jumpFromGround()`.

---

## 🛡️ Anti-Cheat Compatibility Matrix

Because Jump Delay Fix executes the game's genuine jump routine rather than spoofing movement packets, it aligns with standard server movement simulation under vanilla physics:

| Anti-Cheat Solution | Tested Status | Recommended Profile | Notes |
| :--- | :---: | :---: | :--- |
| **GrimAC** | Compatible | `SMART` / `COMPETITIVE` | GrimAC performs 1:1 client simulation. Valid ground contact matches Grim's prediction engine. |
| **Vulcan** | Compatible | `SMART` / `COMPETITIVE` | Standard movement and jump checks pass cleanly. |
| **Polar** | Compatible | `SMART` | Validated in standard testing; use `SMART` for fluctuating latency. |
| **Matrix** | Compatible | `SMART` / `STABLE` | Compatible on standard configurations; strict configs may benefit from `STABLE`. |
| **Intave** | Compatible (profile-dependent) | `SMART` or `STABLE` | Strict servers with high latency require `STABLE` or `SMART` to prevent timing flags. |
| **NoCheatPlus (NCP)** | Compatible | `SMART` / `COMPETITIVE` | Standard survivalfly checks pass when ground ticks match server state. |

> [!NOTE]
> Anti-cheat configurations vary significantly across servers. While Jump Delay Fix executes genuine vanilla jump routines without synthetic movement packets, aggressive server-side heuristics or extreme network jitter can cause desynchronization. If you experience rubberbanding, enable auto profile switching or switch to the `STABLE` profile.

---

## 🧠 Self-Healing Rollback Detection

Even on legitimate servers, network latency fluctuations, server tick drops, or packet loss can cause the server to receive movement packets out of order. When this happens, the server may refuse the jump and "rubberband" the player back to their pre-jump coordinates.

Jump Delay Fix includes a built-in telemetry monitor:
1. **Jump Tracking:** Every jump initiated by the mod records the player's initial Y-coordinate.
2. **Rollback Identification:** On following ticks, the mod examines whether the player established normal upward velocity or was snapped back to the original Y-coordinate.
3. **Automatic Mitigation:**
   - If rollbacks exceed **30%** of recent jumps (or ping surpasses **210 ms**), the mod automatically transitions to the **`STABLE`** profile.
   - `STABLE` adds a conservative +1 tick delay to ensure the server acknowledges ground contact before initiating the next jump.
   - Once server conditions stabilize, the mod transitions back to `SMART` or `COMPETITIVE`.

---

## ⚖️ Multiplayer Server Policies

- **Is Jump Delay Fix a Cheat?**
  No. Jump Delay Fix does not grant flight, speed boosts, step height modifications, or impossible reach. It does not send synthetic packets or modify network payloads. It merely removes the client-side input pause when holding spacebar—an effect that many competitive players previously simulated using high-CPS mouse wheels.
- **Fair Play Recommendation:**
  Always review the specific rules of the multiplayer server you are playing on. Some competitive tournaments or servers have strict policies prohibiting any modification to input timing. When in doubt, you can instantly turn off the mod by pressing **`J`**.
