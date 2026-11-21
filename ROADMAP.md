# Project Roadmap — Jump Delay Fix

This roadmap outlines key milestones, active development tracks, and future vision for **Jump Delay Fix**.

---

## Current Release Track (v1.2.x — Architecture & Engine Decoupling)
- [x] **Instance Decoupling:** Complete dependency injection of `RuntimeState` into `JumpHandler` constructor to eliminate static calls.
- [x] **Levitation Guard:** Ensure `MobEffects.LEVITATION` prevents unintentional jump tick resets during flight or status effect levitation.
- [x] **Architectural Verification:** Enforce loader-agnostic common rules via ArchUnit tests in CI.
- [x] **Multi-Branch Support:** Port engine decoupling cleanly across all active branches (`support/26.2.x`, `support/26.1.x`, `support/1.21.9-1.21.11`).

---

## Upcoming Milestones

### Phase 2: Enhanced In-Game Customization (v1.3.0)
- [ ] **Modern In-Game HUD Indicator:** Subtle, optional on-screen widget displaying current jump reset cooldown ticks.
- [ ] **Dynamic Sensitivity Profiles:** Configurable delay thresholds based on player status (e.g. customized bunny-hop timings for Parkour vs Survival).
- [ ] **ModMenu & NeoForge Config Screen Polish:** Enhanced tooltips, instant keybinding rebinding feedback, and live reset testing.

### Phase 3: Minecraft Modernization & Synchronized Architecture (v2.0.0)
- [x] **Unified Global Versioning Standard:** Synchronized SemVer `2.0.0` architecture with `+mc<RANGE>` build metadata across all active branches.
- [ ] **Next Major Minecraft Release Migration:** Swift porting to upstream snapshot releases via automated mapping pipelines.
- [ ] **Headless GameTest Matrix:** In-engine automated headless player movement tests verifying tick-perfect jump timing across both Fabric and NeoForge.
- [ ] **Automated Semantic Changelogs:** Git cliff or conventional changelog automation hooked into release pipelines.

---

## Long-Term Vision
- **Universal Movement Utility Suite:** Modular ecosystem supporting responsive movement tweaks while upholding 100% anticheat safety across competitive and survival servers.
