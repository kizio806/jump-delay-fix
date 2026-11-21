# Architecture Context — Jump Delay Fix

## Multi-Loader Structure
```text
jump-delay-fix/
├── common/       # Pure logic, config models, JumpHandler, RuntimeState, pure math (No loader/MC imports)
├── fabric/       # Fabric Loom adapter, MixinLivingEntity, FabricKeyMappings, ModMenu integration
└── neoforge/     # NeoForge ModDev adapter, MixinLivingEntity, NeoForgeKeyMappings, ConfigScreen
```

## Key Invariants
- `JumpHandler` receives `RuntimeState` via constructor injection.
- Zero static state mutation inside `JumpHandler`.
- Mixins are narrow, injecting solely at `LivingEntity#jumpFromGround` or `LivingEntity#aiStep` without `@Accessor` or `@Invoker` anti-patterns.
