# Source Hygiene and Comment Policy

## Purpose
In the `jump-delay-fix` codebase, code must maintain high readability and clean architecture. Automated CI checks fail immediately if marker tags or wildcard imports are introduced into Java sources. Meaningful explanatory comments are permitted, but redundant or trivial comments are forbidden. This rule ensures all code remains readable, clean, and passes the strict hygiene gates.

## Standard
- **Meaningful Comments Only:** Explanatory comments are permitted when clarifying complex or non-obvious algorithms, timing heuristics, or cross-loader subtleties. Redundant comments that merely restate self-evident code (e.g., `// check if grounded` before `if (isGrounded)`) are prohibited.
- **No Incomplete Markers:** Never commit `TODO`, `FIXME`, or `XXX` markers in `.java` files. Any unfinished work or technical debt must be tracked via GitHub Issues, not code comments. Enforced by `scripts/validate-source-hygiene.sh`.
- **No Wildcard Imports:** Always import explicit, fully qualified class names. Wildcard package imports (e.g., `import java.util.*;`) are prohibited. Enforced by `scripts/validate-source-hygiene.sh`.
- **Format Integrity:** 4 spaces for indentation, UTF-8 character encoding, Unix line endings (`LF`), and no trailing whitespace. Enforced by Spotless.

## Do:
```java
// Correct: Descriptive method explaining intent clearly
public boolean hasSufficientGroundedTicks(int groundedTicks, int requiredGroundedTicks) {
    return groundedTicks >= requiredGroundedTicks;
}

// Correct: Explanatory comment clarifying a non-obvious timing contract or edge case
// Low ceiling collisions can cause immediate landing before airborne ticks reach the confirmation threshold.
if (jumpLiftObserved && jumpAirborneTicks < MIN_AIRBORNE_TICKS_FOR_CONFIRMATION) {
    onJumpInconclusive();
}

// Correct: Explicit single-class imports
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
```

## Don't:
```java
// Bad: Redundant comment that merely restates what the code does
// Check if player is on ground
if (input.isPlayerOnGround()) {
    ...
}

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
