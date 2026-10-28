# Architecture Assessment

Assessment date: `2026-04-12`

## Executive Verdict

`jump-delay-fix` has a technically sound architecture and is a coherent long-term project.

Why:

- core runtime logic stays in loader-agnostic `common`
- Fabric and NeoForge modules act as focused platform adapters
- release and compatibility gates are automated through Gradle + CI
- test, metadata, structure, and publication checks pass on the current matrix

## Scope And Validation

This assessment was validated locally with:

```bash
./gradlew --no-daemon test
./gradlew --no-daemon publishReadyCheck
```

Result on `2026-04-12`: both commands completed successfully.

## Repository Consistency & Structural Standards

This repository maintains strict engineering consistency through:

- canonical maintainer, release, and security policies (`CONTRIBUTING.md`, `SECURITY.md`)
- unified documentation and wiki layout (`docs/` and `docs/wiki/`)
- automated version-matrix verification and SemVer quality gates
- pre-publication structure validation gate (`validateStructure`)
- explicit line-ending, formatting, and editor rules (`.gitattributes`, `.editorconfig`, Spotless)

## Multi-Loader Artifact & Packaging Contract

Jump Delay Fix enforces a strict **Single JAR per Loader** architectural invariant across all supported Minecraft lines:
- **Module Separation:** Core movement heuristics (`JumpHandler`), profile state (`RuntimeState`, `JumpProfile`), adaptive anti-cheat tracking (`ServerAdaptiveStats`), and configuration (`ConfigManager`) reside exclusively in `:common` as pure Java. No Minecraft or loader classes may be imported in `:common`.
- **Thin Platform Adapters:** `:fabric` and `:neoforge` provide only thin event listeners (`ClientTickEvents` / `ClientTickEvent.Post`), keybinding registration, and configuration screen adapters.
- **Single-JAR Multi-Version Packaging:** Rather than fragmenting releases into multiple patch-specific JARs, each loader module compiles a single production artifact (`jumpdelayfix-fabric-<base_mc>-<ver>.jar` and `jumpdelayfix-neoforge-<base_mc>-<ver>.jar`) that covers the entire patch line via verified dependency ranges (`fabric_minecraft_version_range` and `minecraft_version_range`).
- **Platform Publication Parity:** Modrinth and CurseForge always receive two separate, clean releases per tag (`<version>-fabric` and `<version>-neoforge`), preventing launcher ambiguity while maintaining unified download management.

## Technical Strengths

- clear package naming (`com.kizio.jumpdelayfix`)
- bounded shared state with explicit server-scoped profile memory
- deterministic configuration lifecycle with flush-at-disconnect and shutdown safeguards
- dedicated-server safety checks for NeoForge bytecode references

## Risks To Watch

- auto profile switching depends on evolving multiplayer patterns and should stay telemetry-tested
- upstream Minecraft loader/toolchain releases may require frequent metadata range updates
- Gradle 10 compatibility should be tracked (Gradle 10 deprecation warnings are present in build output)

## Recommended Next Quality Milestones

1. Keep GameTest smoke coverage active for each supported Minecraft patch update.
2. Extend integration assertions for profile auto-switch decisions under edge latency/rollback patterns.
3. Track and resolve Gradle deprecations ahead of the Gradle 10 release.
