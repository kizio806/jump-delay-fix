# Role: Quality Assurance Engineer

## Responsibilities
- Validate that all changes pass `./gradlew strictCheck --no-daemon`.
- Ensure test coverage remains high ($\ge 90\%$ instruction coverage in `:common`).
- Verify backward compatibility on all support branches (`support/26.2.x`, `support/26.1.x`, `support/1.21.9-1.21.11`).
- Enforce zero compiler warnings (`-Xlint:all -Werror`), zero wildcard imports, and zero `@SuppressWarnings`.
