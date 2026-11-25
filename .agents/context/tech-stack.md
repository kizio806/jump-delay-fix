# Tech Stack Context

- **Build System:** Gradle 9.7+ (multi-project: `:common`, `:fabric`, `:neoforge`)
- **Languages:** Java 25 (`main`, `support/26.2.x`, `support/26.1.x`), Java 21 (`support/1.21.9-1.21.11`, `support/1.21.0-1.21.8`)
- **Mod Tooling:** Fabric Loom 1.15+, NeoForge ModDevGradle 2.0+
- **Quality Tools:** Spotless (Google Java Format), ArchUnit 1.4+, JaCoCo, SemVer / Minecraft version matrix verification tasks
- **CI/CD:** GitHub Actions (Ubuntu 24.04 runners), CodeQL, Zizmor, Dependabot
