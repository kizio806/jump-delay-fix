---
name: audit-codebase
description: Audit the jump-delay-fix codebase for architecture violations, code duplication, orphaned files, dead dependencies, and quality gate failures.
---

# Audit Codebase Skill

## When to use it
Use this skill when:
- Beginning work on a new feature or major refactoring.
- Investigating suspected technical debt, code rot, or build discrepancies.
- Checking for orphaned directories, rogue Java classes, or unsanctioned dependencies.
- Auditing multi-loader parity between Fabric and NeoForge.

## Required steps
1. **Repository Layout Verification:**
   - Scan for files outside the canonical multi-loader folders (`common`, `fabric`, `neoforge`).
   - Identify rogue or dead directories (such as the legacy `src/` folder).
2. **Build and Quality Gate Execution:**
   - Run `./gradlew strictCheck publishReadyCheck` to identify compile warnings, formatting errors, or broken contracts.
   - Run `./gradlew --warning-mode all strictCheck` to check for Gradle deprecations.
3. **Source Hygiene and Comments Audit:**
   - Run `./scripts/validate-source-hygiene.sh` to ensure no marker tags (`TODO`, `FIXME`, `XXX`), wildcard imports, or warning suppressions (`@SuppressWarnings`) exist.
4. **Loader Duplication Analysis:**
   - Compare `fabric/src/main/java` and `neoforge/src/main/java` implementations (specifically GUI screens, keybindings, and jump inputs) for copy-paste divergence.
5. **Localization Completeness Check:**
   - Run `LocalizationCompletenessTest` to verify that all 10 language JSON files maintain key and placeholder symmetry.
6. **Test Coverage Analysis:**
   - Run `./gradlew test jacocoTestReport` and inspect `common/build/reports/jacoco/test/html/index.html` to identify untested execution branches in `JumpHandler`, `ConfigManager`, and `RuntimeState`.

## Analysis process
```text
1. Inspect Gradle Configuration
   settings.gradle -> build.gradle -> subprojects build.gradle
2. Scan File System
   find . -type f (compare tracked git files against active Gradle sourceSets)
3. Run Quality Pipeline
   ./gradlew spotlessCheck validateStructure validateSourceHygiene test
4. Inspect Duplication & Coupling
   Diff Fabric vs NeoForge client packages -> Inspect common imports
5. Compile Audit Report
   Document findings, risks, and recommended remediations
```

## Expected output
A structured report detailing:
- Cleanliness status of the working tree and source directories.
- Uncovered architectural defects or orphaned files.
- Current test coverage percentage across instructions and branches.
- Concrete recommendations for refactoring or cleanup.

## Validation checklist
- [ ] Confirmed root `src/` is not compiled by Gradle.
- [ ] Confirmed `common` has no imports of `net.minecraft.*` or mod loaders.
- [ ] Confirmed `./gradlew strictCheck` passed with exit code 0.
- [ ] Verified all 10 language files have zero key mismatches.
- [ ] Checked for Gradle deprecation warnings for future Gradle compatibility.
