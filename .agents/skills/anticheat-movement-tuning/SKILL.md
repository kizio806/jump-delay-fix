---
name: anticheat-movement-tuning
description: Analyze, tune, and test jump delay compensation, latency estimation, airborne lift detection, and adaptive penalty heuristics for multiplayer anticheat compatibility.
---

# Anti-Cheat Movement Tuning Skill

## When to use it
Use this skill when:
- Investigating reported rubberbanding or jump rejections on specific multiplayer servers (GrimAC, Vulcan, Polar, Karhu, Spartan).
- Adjusting latency thresholds, rollback detection sensitivity, or streak recovery parameters in `JumpHandler` and `JumpDelayFix`.
- Adding or modifying jump profiles (`COMPETITIVE`, `SMART`, `STABLE`).

## Required steps
1. **Analyze Anti-Cheat Rejection Mechanics:**
   - Server-side anti-cheats track movement packets (`ServerboundMovePlayerPacket`) and compute client tick timing.
   - If a jump packet arrives before the server has processed the landing packet, the jump is rejected and the player is teleported back to the ground (rubberband).
   - In `JumpHandler`:
     * Lift is verified if `currentY > jumpStartY + 0.005` or `!player.onGround()`.
     * If no lift is observed within `JUMP_NO_LIFT_TIMEOUT_TICKS` (3 ticks), jump is marked rejected (`onJumpRejected()`).
     * If landing happens before `MIN_AIRBORNE_TICKS_FOR_CONFIRMATION` (2 ticks), jump is marked rejected.
2. **Review Profile Characteristics:**
   - `COMPETITIVE`: -1 grounded tick offset, max 1 adaptive penalty tick, 5 successful jumps to reduce penalty. Best for low latency (<=95ms) and low rollback rate (<=8%).
   - `SMART`: 0 grounded tick offset, max 3 adaptive penalty ticks, 3 successful jumps to reduce penalty. Adaptive default.
   - `STABLE`: +1 grounded tick offset, max 5 adaptive penalty ticks, 2 successful jumps to reduce penalty. Safe fallback for high latency (>=210ms) or high rollback rate (>=30%).
3. **Write Unit Tests with Edge-Case Timing:**
   - Create deterministic test scenarios using `FakeJumpInput` simulating latency spikes, packet loss, or server rubberbanding.
   - Verify streak counters and penalty increments in `JumpHandlerTest` and `JumpHandlerIntegrationTest`.
4. **Benchmark Profile Auto-Switching:**
   - Validate `JumpDelayFix.onClientTick()` profile auto-switching decisions with varying `ServerAdaptiveStats`.
5. **Verify No Performance Overhead:**
   - Ensure tick logic maintains $O(1)$ complexity with zero memory allocations per tick.

## Analysis process
```text
1. Mathematical Heuristic Audit
   Required Grounded Ticks = max(1, inputRequired + profileOffset + adaptivePenalty)
2. Rubberband Simulation
   Simulate player position update -> Evaluate lift timeout -> Assert rejection
3. Streak & Recovery Assertion
   Simulate N successful jumps -> Assert adaptive penalty decrements
4. Profile Switch Validation
   Assert transition: SMART -> STABLE on high rollback rate, SMART -> COMPETITIVE on low ping
```

## Expected output
- Rigorously tested adjustments in `JumpHandler.java` or `JumpProfile.java`.
- Comprehensive test coverage in `JumpHandlerIntegrationTest.java`.
- Documented trade-offs in `docs/wiki/Anti-Cheat-and-Fair-Play.md`.

## Validation checklist
- [ ] No allocations during `JumpHandler.tick()`.
- [ ] Adaptive penalty ticks never exceed `profile.maxAdaptivePenaltyTicks()`.
- [ ] Confirmed and rejected jump counters increment accurately.
- [ ] All unit tests in `common/src/test/java/com/kizio/jumpdelayfix/jump/` pass.
