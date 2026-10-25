---
name: new-feature
description: Guide and execute the end-to-end implementation of a new feature in jump-delay-fix following clean architecture, documentation, multi-loader parity, and strict quality gates.
---

# New Feature Skill

## When to use it
Use this skill when:
- Designing and implementing a new feature, profile, user option, or UI component in Jump Delay Fix.
- Adding new configuration keys or localization strings.
- Expanding multiplayer anti-cheat heuristics.

## Required steps
1. **Requirements & Constraints Analysis:**
   - Define player benefits, client-side boundaries, and performance constraints ($O(1)$ tick complexity).
2. **Research Patterns & Minecraft APIs:**
   - Check existing contracts in `common/src/main/java/com/kizio/jumpdelayfix/input/` and `feedback/`.
   - Inspect Minecraft client APIs across Fabric and NeoForge.
3. **Architecture Decision:**
   - Place domain logic in `:common`.
   - Design minimal interface hooks for `:fabric` and `:neoforge`.
   - Update all 10 language JSON files for new strings.
4. **Implementation:**
   - Implement common logic first, followed by loader adapters.
   - Enforce **ZERO** Java comments, **ZERO** TODO markers, and **ZERO** wildcard imports.
5. **Testing & Verification:**
   - Write unit tests in `common/src/test/java/`.
   - Run `./gradlew spotlessApply test strictCheck publishReadyCheck buildAll`.
6. **Documentation & Changelog:**
   - Update `CHANGELOG.md` under unreleased changes and relevant documentation in `docs/wiki/`.

## Analysis process
```text
Requirements -> Research -> Architecture Decision -> Implementation -> Testing -> Review
```

## Expected output
- Fully implemented feature with loader symmetry between Fabric and NeoForge.
- 100% unit test coverage for new business logic in `:common`.
- All 10 language files synchronized with matching keys and placeholders.
- Passing `strictCheck` and `publishReadyCheck`.

## Validation checklist
- [ ] Logic implemented in `common` without Minecraft/loader dependencies.
- [ ] Loader adapters implemented identically in `fabric` and `neoforge`.
- [ ] No Java comments (`//` or `/* */`) in source files.
- [ ] All 10 language files updated and validated via `LocalizationCompletenessTest`.
- [ ] `./gradlew strictCheck` passed.
- [ ] `CHANGELOG.md` updated.
