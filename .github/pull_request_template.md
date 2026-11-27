## Summary

<!-- Brief summary of what this PR accomplishes and why it is needed. -->

## Type of Change

- [ ] 🐛 Bug fix (non-breaking change fixing an issue)
- [ ] ✨ Feature / improvement (non-breaking enhancement)
- [ ] ♻️ Refactor (code improvement without behavior or API change)
- [ ] 🏗️ Build / CI (Gradle, GitHub Actions, toolchains)
- [ ] 📝 Documentation (guides, README, wiki)

## Related Issues

<!-- Closes #123, Fixes #456, etc. -->

## What Changed

-

## Verification & Commands Run

- [ ] `./gradlew spotlessApply` executed to enforce project code style
- [ ] `./gradlew test` passes locally (unit, integration, and ArchUnit tests)
- [ ] `./gradlew strictCheck` passes locally (source hygiene, matrix, SemVer, metadata)
- [ ] `./gradlew buildAll` builds both Fabric and NeoForge production JARs

## Contributor Checklist

- [ ] Behavioral changes are covered by meaningful tests in `common/src/test` (no fake coverage tests)
- [ ] User-visible changes are documented in `CHANGELOG.md`
- [ ] No public API breaks introduced in `:common`
- [ ] Supported Minecraft version matrix and ranges in `gradle.properties` remain untouched (unless approved port)
- [ ] No `@SuppressWarnings` or wildcard imports added (enforced by `validateSourceHygiene`)
- [ ] No `TODO`, `FIXME`, or `XXX` markers left in Java source files
- [ ] `:common` remains 100% loader-agnostic and free of `net.minecraft` classes
- [ ] Dedicated server safety verified (no client-only packages referenced from common or server code)
