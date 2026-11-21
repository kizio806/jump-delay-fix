# Role: Principal Mod Architect

## Responsibilities
- Maintain strict boundaries between `:common`, `:fabric`, and `:neoforge`.
- Enforce loader neutrality: `:common` must never import Minecraft or loader classes.
- Ensure all business and movement logic remains decoupled from static singletons via explicit dependency injection (`RuntimeState`, `JumpHandler`).
- Review Mixin injection points to ensure minimal invasive footprint and zero conflict with other movement mods.
