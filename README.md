# Jump Delay Fix

**Jump Delay Fix** is a lightweight, client-side Minecraft mod designed to provide smoother and more responsive jumping mechanics. It effectively mitigates the frustrating jump delays often experienced on multiplayer servers by adaptively managing jump inputs locally.

## Features

- **Responsive Jumping:** Eliminates the arbitrary delay between jumps, making parkour and fast-paced movement feel significantly better.
- **Adaptive Profiles:** 
  - **Smart (Default):** Automatically balances responsiveness with server acceptance rates.
  - **Competitive:** Maximum responsiveness, optimized for low-latency PVP scenarios.
  - **Stable:** Safest option for high-latency servers or strict anti-cheats.
- **Server Tracking:** Automatically remembers the optimal jump profile for each server you join.
- **Seamless Integration:** Fully compatible with both **Fabric** and **NeoForge**. Works out of the box with zero configuration required.

## Compatibility

- **Minecraft:** 1.21.1
- **Loaders:** Fabric, NeoForge

*This is a client-side only mod. It does not need to be installed on the server to function.*

## Installation

1. Download the latest release from [Modrinth](https://modrinth.com/mod/jump-delay-fix) or the [GitHub Releases](https://github.com/kizio806/jump-delay-fix/releases) page.
2. Ensure you have the corresponding mod loader installed (Fabric Loader or NeoForge).
3. Place the downloaded `.jar` file into your `.minecraft/mods` folder.

## Building from Source

This project uses Gradle. To build the mod for all supported loaders, run the following command in the repository root:

```bash
./gradlew clean buildAll
```

Artifacts will be output to:
- Fabric: `fabric/build/libs/`
- NeoForge: `neoforge/build/libs/`

## Documentation

For developers and contributors, please refer to the following documentation in the `docs/` directory:
- [Architecture & Design](docs/architecture.md)
- [Development Guide](docs/development.md)
- [Release Process](docs/releases.md)

## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for more details.
