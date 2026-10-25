---
name: release
description: Execute the release preparation, quality gate verification, version bumping, changelog update, and publication procedure for jump-delay-fix.
---

# Release Skill

## When to use it
Use this skill when:
- Preparing, verifying, and publishing an official release of Jump Delay Fix.
- Performing dry-run release checks to ensure publication readiness.

## Required steps
1. **Pre-Release Audit:**
   - Confirm clean working tree (`git status --porcelain` is empty).
   - Ensure current branch is `main`, `release/*`, or `support/*`.
   - Run dry-run validation:
     ```bash
     ./scripts/release.sh --dry-run
     ```
2. **Version Bump:**
   - Determine SemVer bump (`PATCH`, `MINOR`, `MAJOR`).
   - Run version script:
     ```bash
     ./scripts/set-minecraft-version.sh \
       --base-minecraft <mc_version> \
       --supported-versions "<mc_versions>" \
       --mod-version <new_semver>
     ```
3. **Changelog Preparation:**
   - Verify `CHANGELOG.md` has an entry with heading `## <new_semver>`.
4. **Build & Artifact Verification:**
   - Verify both Fabric and NeoForge JARs compile and package properly:
     ```bash
     ./gradlew clean buildAll
     ```
5. **Publish Execution:**
   - Execute the automated release command:
     ```bash
     ./scripts/release.sh --publish
     ```
   - This creates the release commit, tags `v<new_semver>`, and pushes to origin to trigger `.github/workflows/release.yml`.

## Analysis process
```text
Audit -> Version Bump -> Changelog -> Build -> Publish
```

## Expected output
- Successfully tagged release (`v<mod_version>`).
- Built artifacts for Fabric and NeoForge.
- Branch and tag pushed to GitHub remote, triggering automated Modrinth and GitHub release deployment.

## Validation checklist
- [ ] Working tree clean before release.
- [ ] `./scripts/release.sh --dry-run` passed all gates.
- [ ] `CHANGELOG.md` has section `## <mod_version>`.
- [ ] `gradle.properties` matches matrix constraints.
- [ ] Tag does not already exist locally or on remote.
- [ ] Release artifacts built and verified.
