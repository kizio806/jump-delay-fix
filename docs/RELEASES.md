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
./gradlew --no-daemon clean buildAll publishReadyCheck
```

## 4. Prepare Changelog

Update `CHANGELOG.md` for user-visible changes for the new `<mod_version>`.

## 5. Tag and Push

Commit the changes, then create and push a git tag matching the `mod_version` to trigger the release workflow.

```bash
git add -A
git commit -m "chore(release): v<mod_version>"
git tag v<mod_version>
git push origin main --tags
```

## 6. Automated Delivery

Pushing the tag triggers the `.github/workflows/release.yml` workflow, which handles the entire release process automatically:

- Build and verification
- Release notes generation
- GitHub Release publishing
- Modrinth upload (if configured)
