# Security Policy

## Supported Versions

Security fixes and advisories are actively provided across all maintained support branches:

| Minecraft Line | Git Branch | Security Support Status |
|---|---|---|
| **Minecraft 26.3** | `main` | ✅ Actively supported |
| **Minecraft 26.2** | `support/26.2.x` | ✅ Actively supported |
| **Minecraft 26.1** | `support/26.1.x` | ✅ Actively supported |
| **Minecraft 1.21.9–1.21.11** | `support/1.21.9-1.21.11` | ✅ Actively supported |
| **Minecraft 1.21.0–1.21.8** | `support/1.21.0-1.21.8` | ✅ Actively supported |
| **Minecraft 1.20.5–1.20.6** | `support/1.20.5-1.20.6` | ✅ Actively supported |
| **Minecraft 1.20.2–1.20.4** | `support/1.20.2-1.20.4` | ✅ Actively supported |
| **Minecraft 1.20.0–1.20.1** | `support/1.20.0-1.20.1` | ✅ Actively supported |

---

## Reporting a Vulnerability

Do not open public GitHub issues for security vulnerabilities.

Please report privately via GitHub Security Advisories or direct maintainer contact:

- Repository Security Portal: https://github.com/kizio806/jump-delay-fix/security

When reporting, please include:
- Affected mod version
- Affected Minecraft and mod loader version
- Detailed steps to reproduce
- Impact assessment and potential client-side risks

---

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
