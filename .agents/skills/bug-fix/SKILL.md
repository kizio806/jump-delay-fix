---
name: bug-fix
description: Guide and execute the end-to-end investigation, reproduction, fix, and regression verification for a bug in jump-delay-fix.
---

# Bug Fix Skill

## When to use it
Use this skill when:
- Investigating reported bugs, rubberbanding issues, configuration corruption, or UI anomalies.
- Fixing test failures, CI workflow breakages, or compiler errors.

## Required steps
1. **Reproduce Defect:**
   - Gather environment details (Minecraft version, loader, server type, anticheat).
   - Write a deterministic failing test case in `common/src/test/java/` that reproduces the bug.
2. **Root Cause Analysis:**
   - Trace the exact failure mechanism in `JumpHandler`, `ConfigStorage`, or loader event listeners.
   - Question assumptions about existing code correctness.
3. **Verify Assumptions:**
   - Confirm whether the issue affects singleplayer, multiplayer, or specific loaders.
   - Evaluate potential side effects of proposed fixes on jump responsiveness.
4. **Implement Targeted Fix:**
   - Apply clean, maintainable solution without defensive workarounds.
   - Strictly **ZERO** Java comments, markers, or wildcard imports.
   - Maintain loader adapter symmetry if loader code is touched.
5. **Regression Testing & Quality Gate:**
   - Verify the reproduction test now passes.
   - Run `./gradlew test strictCheck publishReadyCheck`.
   - Update `CHANGELOG.md` under `### Fixed`.

## Analysis process
```text
Reproduce -> Find Root Cause -> Verify Assumptions -> Fix -> Regression Test
```

## Expected output
- Passing regression test confirming the bug is permanently eliminated.
- Clean code fix adhering strictly to source hygiene rules.
- Documented explanation of root cause and fix in `CHANGELOG.md`.

## Validation checklist
- [ ] Bug reproduced before fix was attempted.
- [ ] Regression test added to test suite.
- [ ] No Java comments (`//` or `/* */`) added in source files.
- [ ] All existing tests and `strictCheck` pass.
- [ ] `CHANGELOG.md` entry added.
