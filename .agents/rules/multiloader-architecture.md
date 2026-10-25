# Multi-Loader Architecture and Boundary Isolation

## Purpose
`jump-delay-fix` targets both Fabric and NeoForge mod loaders from a single repository. The `:common` module houses the loader-agnostic domain logic and must remain completely decoupled from Minecraft and mod loader APIs. This decoupling allows instantaneous JVM unit testing (<1 second) and prevents platform-specific dependencies from contaminating business logic.

## Standard
- **Pure Java Common:** `:common` must only depend on the standard Java 25 library and `org.slf4j:slf4j-api`. It must never import `net.minecraft.*`, `net.fabricmc.*`, or `net.neoforged.*`.
- **Loader Adapter Responsibility:** `:fabric` and `:neoforge` subprojects are adapters that implement input providers (`JumpInput`), GUI screens (`FabricSettingsScreen`, `NeoForgeSettingsScreen`), keybindings, and event hooks. They bridge loader/Minecraft events to `JumpDelayFix`.
- **Avoid Divergence:** When updating or adding features to GUI or input logic in loader modules, update both `:fabric` and `:neoforge` in tandem. Do not introduce discrepancies in user experience or configuration behavior.
- **Root `src/` Prohibition:** The root `src/` directory has been removed and is strictly prohibited by `scripts/validate-structure.sh`. All active code lives exclusively in `common/src`, `fabric/src`, and `neoforge/src`.

## Do:
```java
// Correct: In common/src/main/java/com/kizio/jumpdelayfix/input/JumpInput.java
// Pure Java interface defining what the jumping logic needs from the game client
package com.kizio.jumpdelayfix.input;

public interface JumpInput {
    boolean isJumpPressed();
    boolean isPlayerOnGround();
    void jump();
    double getPlayerY();
    int requiredGroundedTicksBeforeJump();
    int getLatencyMs();
    String getServerIdentifier();
}

// Correct: In fabric/src/main/java and neoforge/src/main/java
// Thin platform-specific implementations that implement JumpInput
```

## Don't:
```java
// Bad: Importing Minecraft or loader classes in common
package com.kizio.jumpdelayfix.jump;

import net.minecraft.client.Minecraft; // FORBIDDEN IN COMMON!
import net.fabricmc.api.EnvType;      // FORBIDDEN IN COMMON!

public class JumpHandler {
    private Minecraft mc; // Violates loader boundary
}

// Bad: Editing root src/ directory
// Editing files in ./src/main/java/... has NO effect on the build!
```

## Verification:
An AI agent must verify that no platform or loader imports exist in `common`:
```bash
./scripts/validate-structure.sh
```
Or run ripgrep directly:
```bash
rg 'import (net\.fabricmc|net\.neoforged|net\.minecraft)\.' common/src/main/java
```
Output must be empty.
