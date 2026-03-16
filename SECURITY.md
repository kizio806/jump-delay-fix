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

## Secret Management

- Never hardcode tokens in code or workflows.
- Use GitHub Actions secrets for release credentials.
- Required release secrets:
  - `MODRINTH_TOKEN`
  - `MODRINTH_PROJECT_ID`
