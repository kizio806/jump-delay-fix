# Jump Delay Fix — AI Agent Guidelines & Architecture Manual

This document defines the mandatory operating standards, behavioral guidelines, and development lifecycle for AI agents and human engineers working on the **Jump Delay Fix** codebase.

Future AI agents working in this repository must operate as **experienced senior developers and principal software architects**: questioning assumptions, verifying facts against bytecode and test execution, avoiding shortcuts, and upholding strict technical excellence.

---

## 1. Core Engineering Philosophy & Behavioral Rules

### Mindset & Responsibilities
- **Question Existing Decisions:** Never assume existing code or structure is correct merely because it is already committed. In this repository, historical refactorings left an orphaned `src/` directory at the root that Gradle completely ignored (now permanently removed and blocked by `validateStructure`). Always verify what the build system actually compiles.
- **Search for Better Solutions:** Investigate clean abstractions rather than blindly repeating existing patterns. For example, rather than duplicating Minecraft client GUI code between `fabric/` and `neoforge/`, evaluate shared abstractions.
- **Prefer Maintainable & Simple Solutions:** Do not introduce heavy frameworks, reflection tricks, bytecode manipulation, or third-party dependencies when simple, pure-Java algorithms suffice.
- **Explain Trade-offs Explicitly:** When proposing changes, outline the pros, cons, and performance implications (e.g., pure Java in `common` keeps unit tests running in <1s, but requires loader adapters for Minecraft client APIs).
- **Avoid Shortcuts:** Never bypass `./gradlew strictCheck`, never disable quality gates, never suppress compiler warnings (`-Xlint:all -Werror` is enforced), and never leave uncommitted scratch files.

### Critical "Do NOT" Rules
- **Do NOT blindly copy current architecture:** Scrutinize the boundaries between `common`, `fabric`, and `neoforge`.
- **Do NOT add unnecessary complexity:** Keep the mod lightweight; its primary value is zero-overhead responsive jumping.
- **Do NOT create rules or assertions without evidence:** Every constraint must be grounded in Gradle tasks, bash validation scripts, or Minecraft runtime requirements.
- **Do NOT assume existing code is correct:** Always run tests, inspect bytecode dependencies, and check git blame/history.
- **Do NOT add trivial or redundant comments:** Meaningful explanatory comments are permitted when clarifying non-obvious algorithms or contracts, but redundant comments that merely restate the code, `TODO`/`FIXME`/`XXX` tags, and wildcard imports are prohibited.
- **Do NOT suppress warnings:** `@SuppressWarnings` and warning-silencing annotations of any kind are strictly forbidden. Resolve deprecations, type mismatches, and linter warnings at the source.

---

## 2. Before Coding: Discovery & Planning

Before touching any code, an AI agent must complete four distinct analysis steps:

### Step 1: Understand the Architecture
1. **Module Separation:**
   - `:common`: Pure Java 25 library. Loader-agnostic and Minecraft-free. Contains core jumping heuristics (`JumpHandler`), configuration management (`ConfigManager`, `ConfigStorage`), profile state (`RuntimeState`, `JumpProfile`), adaptive anti-cheat statistics (`ServerAdaptiveStats`), and input/feedback interfaces (`JumpInput`, `ToggleFeedback`).
   - `:fabric`: Fabric Loom subproject. Thin client adapter implementing Fabric keybindings (`FabricKeyMappings`), client tick listeners (`ClientTickEvents`), screens (`FabricSettingsScreen`), and input bridge (`FabricJumpInput`).
   - `:neoforge`: NeoForge ModDev subproject. Thin client adapter implementing NeoForge keybindings (`NeoForgeKeyMappings`), client event bus listeners (`NeoForgeClient`), screens (`NeoForgeSettingsScreen`), and input bridge (`NeoForgeJumpInput`).
2. **Execution Flow:**
   - The mod does **NOT** use Mixins (accessors/invokers are strictly banned by `scripts/validate-structure.sh`).
   - Instead, it hooks into client end-tick events (`END_CLIENT_TICK` on Fabric, `ClientTickEvent.Post` on NeoForge).
   - Every tick, `JumpHandler.tick()` evaluates player grounded ticks, latency, and input buffers. If criteria are met, it invokes `player.jumpFromGround()`.
   - Post-jump lift and airborne ticks are tracked to detect rubberbanding or anti-cheat rollbacks, dynamically adjusting `adaptivePenaltyTicks` and server profile assignments.

### Step 2: Inspect Existing Patterns & Caveats
- **Static State vs. Instance Encapsulation:** `JumpDelayFix` manages static singleton state. Be aware of state reset methods (`resetTransientState()`, `resetForTests()`) required between test cases or player disconnects.
- **Atomic Configuration Persistence:** Config is stored in `.properties` format under `config/jumpdelayfix.properties`, written atomically via temporary files and `StandardCopyOption.ATOMIC_MOVE`. Server IDs are Base64-encoded to support arbitrary server IP/domain characters.
- **Localization Contracts:** All user-facing strings must exist in all 10 supported locale files (`en_us.json`, `pl_pl.json`, `de_de.json`, `es_es.json`, `fr_fr.json`, `it_it.json`, `ja_jp.json`, `pt_br.json`, `ru_ru.json`, `zh_cn.json`). `LocalizationCompletenessTest` validates key and placeholder parity across all locales.

### Step 3: Check Documentation & Repository Guidelines
- Read `docs/architecture.md`, `docs/development.md`, and `docs/releases.md`.
- Read wiki articles in `docs/wiki/` (especially `Anti-Cheat-and-Fair-Play.md` and `Configuration.md`).
- Review `.agents/rules/` for detailed coding rules.

### Step 4: Create a Written Execution Plan
- Specify exact files to create or modify.
- Outline how backward compatibility with existing configuration files will be maintained.
- Identify the verification commands that will validate the changes.

---

## 3. During Coding: Standards & Conventions

### 1. Source Hygiene (Hard Gate)
- **Comment Hygiene:** Meaningful, high-value comments explaining complex algorithms or non-obvious contracts are permitted. Redundant, trivial comments that merely narrate self-explanatory code are strictly discouraged.
- **Zero Markers:** No `TODO`, `FIXME`, or `XXX` tags in Java files.
- **Zero Wildcard Imports:** `import java.util.*;` is strictly prohibited. Use explicit single-class imports.
- **Zero Warning Suppressions:** `@SuppressWarnings` is strictly forbidden. Resolve warnings and deprecations using modern non-deprecated APIs. Enforced by `validateSourceHygiene`.

### 2. Code Style & Clean Architecture
- **Formatting:** 4 spaces for indentation, UTF-8 encoding, Unix line endings (`\n`), no trailing whitespace. Enforced by Spotless.
- **Compiler Flags:** Compiles under Java 25 with `-Xlint:all` and `-Werror`. Do not introduce compiler warnings.
- **Boolean Predicates:** Name boolean methods and variables descriptively: `isEnabled`, `hasLiftObserved`, `shouldAutoSwitch`.
- **Modularity:**
  - Never import loader packages (`net.fabricmc.*`, `net.neoforged.*`) in `common`.
  - Never import client packages (`net.minecraft.client.*`) in `common`.
  - In NeoForge, never reference client-only packages from non-client classes (enforced by `:neoforge:verifyDedicatedServerSafe`).
- **Dependencies:** Keep dependencies minimal. The runtime bundle in `:common` must only depend on `org.slf4j:slf4j-api`. Do not add libraries to `common/build.gradle` without principal review.

### 3. Change Safety & Backward Compatibility
- Never break configuration keys in `ConfigStorage` without migration logic.
- When adding new configuration properties, provide safe fallback defaults in `RuntimeConfig.defaults()`.
- When adding GUI components or key mappings, ensure identical behavior on both Fabric and NeoForge.

---

## 4. Before Finishing: Verification & Review

Before concluding any work, an AI agent must run the complete verification gauntlet:

```bash
# 1. Format code according to Spotless
./gradlew spotlessApply

# 2. Run unit and integration tests across all modules
./gradlew test

# 3. Validate structural layout and source hygiene
./gradlew validateStructure validateSourceHygiene

# 4. Verify version matrix and SemVer compliance
./gradlew verifyMinecraftVersionMatrix verifySemverVersion

# 5. Execute full release readiness and quality gates
./gradlew strictCheck publishReadyCheck

# 6. Verify full build of both loader JARs
./gradlew buildAll
```

### Self-Review Checklist
- [ ] Run `git status` to ensure no untracked scratch files or unintended changes exist.
- [ ] Run `git diff` to review every changed line against project standards.
- [ ] Ensure `scripts/validate-source-hygiene.sh` passes cleanly (no TODOs, no wildcard imports).
- [ ] If user-facing strings were changed, verify all 10 language JSON files match (`LocalizationCompletenessTest`).
- [ ] If Minecraft version metadata was touched, ensure `gradle.properties` matches the single patch-line requirement (`verifyMinecraftVersionMatrix`).
- [ ] Update `CHANGELOG.md` under the appropriate version heading if changes are user-visible.

---

## 5. Directory & Customization Architecture

Customizations and operational instructions are organized as follows:
- **`AGENTS.md` / `.agents/AGENTS.md`**: Main instruction and architecture guide (this document).
- **`.agents/rules/`**: Specialized rules detailing standards, DOs, DON'Ts, and verification procedures.
- **`.agents/skills/`**: Interactive, multi-step procedures for auditing, version porting, quality gate validation, and loader adaptation.
- **`.agents/workflows/`**: Process lifecycles for New Features, Bug Fixes, and Releases.
