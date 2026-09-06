# Jump Delay Fix

[![CI](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml/badge.svg)](https://github.com/kizio806/jump-delay-fix/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/kizio806/jump-delay-fix?sort=semver)](https://github.com/kizio806/jump-delay-fix/releases)
[![Modrinth](https://img.shields.io/badge/Modrinth-release%20ready-00AF5C?logo=modrinth)](docs/modrinth.md)
[![License](https://img.shields.io/github/license/kizio806/jump-delay-fix)](LICENSE)

Jump Delay Fix is a lightweight, client-side Minecraft mod designed to provide smoother and more responsive jumping. By addressing the vanilla auto-jump delay mechanism, it offers a consistent jumping experience, especially useful for parkour and intense gameplay scenarios.

## Highlights

- **Instant Responsiveness:** Removes the hardcoded vanilla jump delay for immediate action.
- **Client-Side Only:** No server installation required. Works entirely on the client, making it multiplayer-friendly.
- **Multi-Loader Support:** Compatible with both Fabric and NeoForge out of the box.

## Supported Platforms & Versions

This branch (`main`) is currently targeting **Minecraft 26.2**.

- **Fabric Loader:** 0.18.4+ (Fabric API 0.159.0+26.2)
- **NeoForge:** 26.2.0.79+
- **Minecraft:** 26.2

*(See `gradle.properties` for the exact current version metadata.)*

## Installation

1. Download the latest release from the [Releases page](https://github.com/kizio806/jump-delay-fix/releases) or [Modrinth](https://modrinth.com/mod/jump-delay-fix).
2. Ensure you have the correct mod loader installed (Fabric + Fabric API, or NeoForge) for Minecraft 26.2.
3. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
4. Launch the game.

## Building from Source

**Requirements:**
- Java 25
- Gradle wrapper (`./gradlew`)

**Commands:**
```bash
# Clean, build, and run strict checks
./gradlew --no-daemon clean buildAll strictCheck

# Run release publication checks
./gradlew --no-daemon publishReadyCheck
```

## Architecture

The project uses a multi-project Gradle layout:
- `common`: Contains shared rules and configuration logic.
- `fabric`: Fabric bootstrap and client adapters.
- `neoforge`: NeoForge bootstrap and client adapters.

## Project Documentation

- **Wiki:** [docs/wiki/Home.md](docs/wiki/Home.md)
- **Development & Code Style:** [docs/development.md](docs/development.md)
- **Architecture:** [docs/architecture.md](docs/architecture.md)
- **Releases Guide:** [docs/releases.md](docs/releases.md)
- **Modrinth Guide:** [docs/modrinth.md](docs/modrinth.md)
- **Changelog:** [CHANGELOG.md](CHANGELOG.md)
- **Contributing:** [CONTRIBUTING.md](CONTRIBUTING.md)
- **Security:** [SECURITY.md](SECURITY.md)

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
