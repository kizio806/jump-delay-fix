# Agent Rule: Versioning and Release Management

This rule governs how AI agents and automated tools must handle versioning, branching, and release operations across this repository.

## Non-Negotiable AI Directives

AI agents must NEVER independently invent version numbers, bypass branch compatibility mappings, or diverge from the global synchronization policy.

When performing version-related or cross-branch work, every agent MUST adhere to the following 10 directives:

1. **Read Authoritative Policy First**: Always read [docs/releases.md](../../docs/releases.md) before modifying any project version metadata or build configuration.
2. **Identify All Active Branches**: Inspect all active compatibility branches (`main`, `support/26.2.x`, `support/26.1.x`, `support/1.21.9-1.21.11`).
3. **Determine Global Base Version**: Determine the single authoritative base version (`MAJOR.MINOR.PATCH`, currently `2.0.0`).
4. **Apply Uniform Base Version**: Apply the exact same base version across all active branches without exception.
5. **Adapt Implementation Per Line**: Adapt code changes, mappings, and loader APIs to each supported Minecraft line while maintaining behavioral parity.
6. **Synchronize Tests & Metadata**: Update unit tests, metadata contract tests, and `gradle.properties` consistently on every active branch.
7. **Verify Standard Artifacts**: Verify that both required artifacts (`jump-delay-fix-<BASE>-<RANGE>-fabric.jar` and `jump-delay-fix-<BASE>-<RANGE>-neoforge.jar`) build cleanly.
8. **Never Publish Unauthorized**: Never execute public release publication, tag pushes, or package uploads without explicit user authorization.
9. **Preserve Historical State**: Never delete, overwrite, or rewrite historical git tags, changelog records, or releases.
10. **Zero Branch Version Drift**: Never introduce an independent version number or alternate versioning sequence for any individual support branch.
