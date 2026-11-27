---
name: verify-quality-gates
description: Run the full quality and verification pipeline including source hygiene, Spotless formatting, multi-loader contract tests, dedicated server safety, and matrix validation.
---

# Verify Quality Gates Skill

## When to use it
Use this skill when:
- Preparing to commit changes or create a pull request.
- Verifying the workspace before initiating a release.
- Diagnosing why a CI workflow failed on GitHub Actions.

## Required steps
1. **Apply Formatting:**
   ```bash
   ./gradlew spotlessApply
   ```
2. **Execute Source Hygiene & Structural Checks:**
   ```bash
   ./scripts/validate-structure.sh
   ./scripts/validate-source-hygiene.sh
   ```
3. **Run Unit and Contract Tests:**
   ```bash
   ./gradlew test
   ```
4. **Execute Dedicated Server Bytecode Verification:**
   ```bash
   ./gradlew :neoforge:verifyDedicatedServerSafe
   ```
5. **Verify Version Matrix and SemVer Compliance:**
   ```bash
   ./gradlew verifyMinecraftVersionMatrix verifySemverVersion
   ```
6. **Execute Full Release Readiness:**
   ```bash
   ./gradlew publishReadyCheck strictCheck
   ```

## Analysis process
```text
1. Spotless Format Check
   Validates 4-space indent, newline at EOF, no trailing whitespace
2. Hygiene Validation
   Scans for TODO/FIXME/XXX markers, wildcard imports, and @SuppressWarnings
3. Multi-Loader Safety
   Bytecode scan for client class references in dedicated server classes
4. Test Execution
   Common unit tests + Fabric/NeoForge contract tests
5. Matrix Verification
   Validates version ranges and SemVer rules in gradle.properties
```

## Expected output
- All tasks report `SUCCESSFUL` or `UP-TO-DATE`.
- Clean working directory with no untracked temporary files.
- Test reports in `common/build/reports/tests/test/` and `fabric/build/reports/tests/test/`.

## Validation checklist
- [ ] No marker tags (TODO/FIXME/XXX) or @SuppressWarnings in `common/`, `fabric/`, or `neoforge/`.
- [ ] No wildcard imports in Java files.
- [ ] Zero compiler warnings (`-Xlint:all -Werror` active).
- [ ] `:neoforge:verifyDedicatedServerSafe` reported no offending classes.
- [ ] `strictCheck` and `publishReadyCheck` exit with code 0.
