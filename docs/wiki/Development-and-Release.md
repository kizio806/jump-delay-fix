# Development and Release

## Prerequisites

- JDK 25
- Git

## Building

```bash
./gradlew clean buildAll
```

Output JARs:

- `fabric/build/libs/jumpdelayfix-fabric-<mc>-<version>.jar`
- `neoforge/build/libs/jumpdelayfix-neoforge-<mc>-<version>.jar`

## Running Tests

```bash
./gradlew test
```

## Full Validation

```bash
./gradlew clean buildAll strictCheck
```

This runs:

- Unit tests (common)
- Fabric GameTest server
- NeoForge GameTest server
- Structure validation
- Source hygiene checks
- Semver validation
- Metadata checks
- Modrinth/Fabric/NeoForge compatibility gates

## Release Process

1. Update `mod_version` in `gradle.properties`.
2. Update `CHANGELOG.md`.
3. Run `./gradlew clean buildAll strictCheck`.
4. Commit and push.
5. Tag: `git tag v<version> && git push origin v<version>`.

GitHub Actions handles the rest: GitHub Release creation, JAR upload, and Modrinth publishing.

## Modrinth Secrets

Set the following in GitHub Repository Secrets:

- `MODRINTH_TOKEN` — API token from [modrinth.com/settings/pats](https://modrinth.com/settings/pats)
- `MODRINTH_PROJECT_ID` — the Modrinth project slug or ID
