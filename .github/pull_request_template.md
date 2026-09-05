## Summary

<!--
What does this PR do? One or two sentences max.
-->

## Type of change

- [ ] 🐛 Bug fix
- [ ] ✨ Feature / improvement
- [ ] ♻️ Refactor (no behavior change)
- [ ] 🏗️ Build / CI
- [ ] 📝 Documentation

## Related issues

<!--
Closes #<issue_number>
-->

## What changed

<!--
Bullet list of concrete changes. Be specific — reviewers should not need to read every file.
- Changed X to Y because Z
- Added validation for ...
- Removed unused class Foo
-->

## Validation

<!--
Check every box that applies. Do not check something you haven't done.
-->

- [ ] `./gradlew clean buildAll strictCheck` passes locally
- [ ] Tested on Fabric (client or game test server)
- [ ] Tested on NeoForge (client or game test server)
- [ ] Dedicated server safety verified (NeoForge)

## Checklist

- [ ] `CHANGELOG.md` updated for user-visible changes
- [ ] No `TODO` / `FIXME` / wildcard imports added to source
- [ ] Metadata (`fabric.mod.json`, `mods.toml`) is consistent with `gradle.properties`
- [ ] Multi-version ranges (`fabric_game_versions`, `neoforge_game_versions`, `modrinth_game_versions`) remain valid
- [ ] No client-only classes referenced from common or dedicated-server code
