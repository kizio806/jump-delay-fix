# Security Policy

## Supported Versions

Security fixes are applied to the latest active release line.

## Reporting a Vulnerability

Do not open public issues for security vulnerabilities.

Please report privately via GitHub Security Advisories or direct maintainer contact:

- Repository: https://github.com/kizio806/jump-delay-fix/security

Include:

- Affected mod version
- Affected Minecraft/loader version
- Reproduction details
- Impact assessment

## Secret & Credential Management

- Never hardcode tokens or credentials in source code or workflow files.
- Credentials and project identifiers must be managed exclusively through GitHub Actions Secrets or standard platform contexts.

### 1. Platform-Generated Token
- `GITHUB_TOKEN`: Ephemeral installation token provisioned automatically by GitHub Actions for each workflow execution. It is never manually configured. Used for PR labeling (`labeler.yml`), wiki synchronization (`wiki-sync.yml`), and creating GitHub Releases (`release.yml`).

### 2. External Service Authentication Tokens (Sensitive Secrets)
- `MODRINTH_TOKEN`: Personal API token with version publishing permissions on Modrinth.
- `CURSEFORGE_TOKEN`: API token with file upload permissions on CurseForge.
- `SONAR_TOKEN`: Authentication token for SonarCloud static analysis reporting.
- `CODECOV_TOKEN`: Repository upload token for Codecov test coverage reports.

### 3. Project Identifiers & Configuration Variables (Non-Token References)
- `MODRINTH_PROJECT_ID`: Unique project identifier (or slug) target on Modrinth.
- `CURSEFORGE_PROJECT_ID`: Numerical project identifier target on CurseForge.
