# Release Management

## 1. Probe Candidate Versions

Run compatibility probe before changing published metadata to determine which versions are safe to target.

Example:
```bash
./scripts/probe-minecraft-versions.sh \
  --versions "1.21.9,1.21.10,1.21.11"
```

## 2. Update Version Metadata

Use the helper script to set the new version details in `gradle.properties`.

Example:
```bash
./scripts/set-minecraft-version.sh \
  --base-minecraft <minecraft_version> \
  --fabric-supported-versions "<supported_versions>" \
  --neoforge-supported-versions "<supported_versions>" \
  --mod-version <mod_version>
```

## 3. Verify

Ensure the project builds correctly with the new metadata.

```bash
./gradlew --no-daemon --stacktrace clean buildAll strictCheck
```

## 4. Prepare Changelog

Update `CHANGELOG.md` for user-visible changes for the new `<mod_version>`.
That section becomes the release body for both GitHub Releases and Modrinth.

## 5. One-Command Release Procedure

To perform the release safely, use the included release script:

```bash
# 1. First run dry-run verification
./scripts/release.sh --dry-run

# 2. When ready, publish the release with one command:
./scripts/release.sh --publish
```

The script automatically:
1. Validates source hygiene, structure, and semver compliance.
2. Executes strict compilation and pre-publish verification gates.
3. Builds release artifacts for all supported loaders.
4. Verifies artifact existence and file sizes.
5. Creates the release commit (`chore(release): v<mod_version>`) and annotated tag (`v<mod_version>`).
6. Pushes the branch and tag to GitHub.

## 6. Automated Delivery

Pushing the release tag triggers `.github/workflows/release.yml`, which handles the entire deployment process automatically:

- Clean environment build and verification
- Release notes generation from `CHANGELOG.md`
- SHA-256 checksum generation
- GitHub Release publishing with Fabric and NeoForge JARs
- Modrinth publishing for Fabric and NeoForge (using configured secrets)
- CurseForge publishing (if configured)
