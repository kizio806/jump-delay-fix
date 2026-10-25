# Localization and Resource Asset Integrity

## Purpose
Jump Delay Fix is localized into 10 languages: English (`en_us`), Polish (`pl_pl`), German (`de_de`), Spanish (`es_es`), French (`fr_fr`), Italian (`it_it`), Japanese (`ja_jp`), Portuguese (`pt_br`), Russian (`ru_ru`), and Chinese (`zh_cn`). To avoid broken translations or missing keys at runtime, `LocalizationCompletenessTest` strictly checks that every language file contains the exact same translation keys and parameter placeholders as `en_us.json`.

## Standard
- **Key Parity:** Whenever a new key is added or modified in `en_us.json`, all other 9 language JSON files must be updated simultaneously.
- **Placeholder Parity:** Every translation entry must have the exact same number of format placeholders (`%s`, `%d`, `%1$s`) across all language files.
- **Asset Dimensions:** The mod icon `common/src/main/resources/assets/jumpdelayfix/icon.png` must be exactly 512x512 pixels. This is validated by `:fabric:validateProductionMetadata` and `:neoforge:validateProductionMetadata`.
- **Resource Duplication:** Loader modules inherit resources directly from `common/src/main/resources`. Do not duplicate translation files or mod icons in `fabric/` or `neoforge/`.

## Do:
```json
// In common/src/main/resources/assets/jumpdelayfix/lang/en_us.json:
"gui.jumpdelayfix.option.done": "Done",
"message.jumpdelayfix.profile_status": "Active profile: %s"

// In common/src/main/resources/assets/jumpdelayfix/lang/pl_pl.json:
"gui.jumpdelayfix.option.done": "Gotowe",
"message.jumpdelayfix.profile_status": "Aktywny profil: %s"
```

## Don't:
```json
// Bad: Adding a new key to en_us.json but omitting it in other languages
// Will cause LocalizationCompletenessTest to fail:
// "Locale keys mismatch for pl_pl.json"

// Bad: Changing placeholder count in translation:
// en_us: "%s: %s" (2 placeholders)
// de_de: "%s" (1 placeholder -> FAILS TEST!)
```

## Verification:
An AI agent must run the localization test to verify language file consistency:
```bash
./gradlew :common:test --tests "com.kizio.jumpdelayfix.LocalizationCompletenessTest"
```
And verify icon assets:
```bash
./gradlew :fabric:validateProductionMetadata :neoforge:validateProductionMetadata
```
All tasks must succeed.
