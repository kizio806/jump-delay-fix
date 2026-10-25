# Quality Gates, Testing, and Verification

## Purpose
The repository enforces a strict zero-tolerance quality pipeline (`strictCheck` and `publishReadyCheck`). Every pull request and release is validated on CI against compiler warnings, code coverage thresholds, and multi-loader contracts. Changes that pass locally must also pass the automated pre-release verification gates.

## Standard
- **Strict Compiler Settings:** All Java subprojects compile with `-Xlint:all` and `-Werror`. No compiler warnings (unchecked conversions, deprecations, raw types, or missing serial IDs) may be left unresolved.
- **Unit & Integration Testing:** All business logic in `:common` must be covered by JUnit 5/6 tests. Maintain at least 90% instruction coverage and 80% branch coverage on `:common`.
- **Contract Tests:** Any modification to metadata files (`fabric.mod.json`, `neoforge.mods.toml`) must maintain valid properties, matching mod ID, version, license, and icons as validated by `FabricMetadataContractTest` and `NeoForgeMetadataContractTest`.
- **Dedicated Server Safety:** Non-client classes in `:neoforge` must pass bytecode scanning to ensure they never import or reference client-only classes.

## Do:
```java
// Correct: Isolated unit tests using mock or fake implementations in common
class JumpHandlerTest {
    @Test
    void shouldNotTriggerJumpWhenPlayerIsNotGrounded() {
        FakeJumpInput input = new FakeJumpInput();
        input.setOnGround(false);
        input.setJumpPressed(true);

        JumpHandler handler = new JumpHandler(input);
        handler.tick();

        assertFalse(input.hasJumped());
    }
}
```

## Don't:
```java
// Bad: Writing tests that rely on external files, network connections, or hardcoded absolute paths
// Bad: Skipping strictCheck before pushing
// Bad: Suppressing warnings with @SuppressWarnings without explicit architect justification
```

## Verification:
An AI agent must run the complete suite of quality tasks before marking work complete:
```bash
./gradlew test
./gradlew strictCheck publishReadyCheck
```
Both tasks must exit with `BUILD SUCCESSFUL`.
