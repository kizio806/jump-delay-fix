# Contributing to Jump Delay Fix

## Development Setup

1. Install Java `21`.
2. Clone the repository.
3. Run verification:

```bash
./gradlew --no-daemon clean buildAll publishReadyCheck
```

## Branching

- `main`: stable development line
- `release/*`: release stabilization branches (optional)
- `feature/*`: new features
- `fix/*`: bug fixes
- `chore/*`: build/docs/maintenance

## Commit Style

Use clear conventional messages:

- `feat: add ...`
- `fix: correct ...`
- `refactor: simplify ...`
- `build: update ...`
- `docs: improve ...`

## Pull Request Checklist

- [ ] `./gradlew --no-daemon clean buildAll publishReadyCheck` passes locally
- [ ] Fabric and NeoForge behavior remains equivalent for shared features
- [ ] No server-only entrypoint references client classes
- [ ] User-visible changes are documented in `CHANGELOG.md`

## Code Guidelines

- Keep gameplay logic in `common` whenever possible.
- Keep loader modules thin and adapter-oriented.
- Avoid duplicated logic between Fabric and NeoForge.
- Add tests for behavioral changes in `common/src/test`.
- Do not commit secrets or local environment files.
