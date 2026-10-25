# Client Safety, Bytecode Isolation, and Mixin Prohibition

## Purpose
`jump-delay-fix` is a pure client-side mod. However, if a client mod accidentally introduces hard references to client-only classes in common/root initialization paths, installing the mod on a dedicated server will crash the server JVM with `NoClassDefFoundError` or `ClassNotFoundException`. Furthermore, fragile mixins (`@Accessor`, `@Invoker`) are forbidden to avoid binary breakage across Minecraft point releases.

## Standard
- **No Fragile Mixins:** Accessor and invoker mixins (`@Accessor`, `@Invoker`) are strictly forbidden by `scripts/validate-structure.sh`. The mod achieves its functionality using clean client tick event hooks and official Minecraft API methods.
- **Dedicated Server Guarding:**
  - On Fabric: Use `@Environment(EnvType.CLIENT)` on client initializers and screens. The mod declares `"environment": "client"` in `fabric.mod.json`.
  - On NeoForge: Guard client bootstrap using `FMLEnvironment.getDist() == Dist.CLIENT` before calling any client class. Use `@OnlyIn(Dist.CLIENT)` on client adapters.
- **Bytecode Inspection:** The task `:neoforge:verifyDedicatedServerSafe` scans compiled `.class` files in non-client packages to ensure no string references to `net/minecraft/client`, `net/neoforged/neoforge/client`, or `com/terraformersmc/modmenu` exist.

## Do:
```java
// Correct: NeoForge main mod constructor guarding client setup
package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public final class JumpDelayFixNeoForge {
    public JumpDelayFixNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForgeClient.init(modEventBus);
        }
    }
}
```

## Don't:
```java
// Bad: Direct call to client methods without Dist.CLIENT guard
@Mod(Constants.MOD_ID)
public final class JumpDelayFixNeoForge {
    public JumpDelayFixNeoForge() {
        Minecraft.getInstance(); // Crashes immediately on dedicated server!
    }
}

// Bad: Creating Mixin accessors
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumpDelay") // Strictly forbidden by scripts/validate-structure.sh!
    int getJumpDelay();
}
```

## Verification:
An AI agent must verify that structure checks and dedicated server bytecode scans pass:
```bash
./scripts/validate-structure.sh
./gradlew :neoforge:verifyDedicatedServerSafe
```
Output must confirm structure validation passed and no server safety violations occurred.
