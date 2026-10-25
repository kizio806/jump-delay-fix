# Source Hygiene and Comment Policy

## Purpose
In the `jump-delay-fix` codebase, code must be entirely self-documenting and strictly formatted. Automated CI checks fail immediately if comments, marker tags, or wildcard imports are introduced into Java sources. This rule ensures all code remains readable, clean, and passes the strict hygiene gates.

## Standard
- **No Java Comments:** Never write line comments (`//`) or block comments (`/* */`) in `.java` source or test files. Code intent must be made clear through expressive class, method, and variable naming, and documented in dedicated Markdown files under `docs/` or commit messages.
- **No Incomplete Markers:** Never commit `TODO`, `FIXME`, or `XXX` markers in `.java` files. Any unfinished work or technical debt must be tracked via GitHub Issues, not code comments.
- **No Wildcard Imports:** Always import explicit, fully qualified class names. Wildcard package imports (e.g., `import java.util.*;`) are prohibited.
- **Format Integrity:** 4 spaces for indentation, UTF-8 character encoding, Unix line endings (`LF`), and no trailing whitespace.

## Do:
```java
// Correct: Self-documenting boolean predicates and descriptive identifiers
public boolean hasSufficientGroundedTicks(int groundedTicks, int requiredGroundedTicks) {
    return groundedTicks >= requiredGroundedTicks;
}

// Correct: Explicit single-class imports
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
```

## Don't:
```java
// Bad: Line comments will fail scripts/validate-source-hygiene.sh
// Check if player is on ground
if (input.isPlayerOnGround()) {
    ...
}

/* Bad: Block comments will fail scripts/validate-source-hygiene.sh */
/*
 * Profile logic
 */

// Bad: Marker tags will fail scripts/validate-source-hygiene.sh
// TODO: Fix latency spike under poor WiFi

// Bad: Wildcard import will fail scripts/validate-source-hygiene.sh
import java.util.*;
```

## Verification:
An AI agent must check compliance before submitting any changes by running:
```bash
./scripts/validate-source-hygiene.sh
```
Or via Gradle:
```bash
./gradlew validateSourceHygiene
```
Expected output:
```text
Source hygiene validation passed.
```
