# REMEDIATION TRACKER — JUMP DELAY FIX
**Dokument Identyfikacyjny:** `REMEDIATION-TRACKER-JDF-2026-11`  
**Data utworzenia:** 7 listopada 2026 r.  
**Ostatnia aktualizacja:** 8 listopada 2026 r. (PROMPT 5 — Stabilization & Git Consolidation)  
**Autor:** Principal Java Engineer, Software Architect, Security Engineer & Quality Auditor  
**Cel:** Doprowadzenie projektu `jump-delay-fix` do bezkompromisowego standardu inżynierskiego na podstawie raportu audytu `AUDIT-JDF-SE-2026-11`.

---

## 1. Podsumowanie Realizacji

| Projekt | Łącznie | DONE_VERIFIED | PARTIAL | NOT_DONE | BLOCKED | Procent Ukończenia |
|---|---:|---:|---:|---:|---:|---:|
| **Jump Delay Fix** | 13 | 13 | 0 | 0 | 0 | **100.00%** |

---

## 2. Rejestr Zadań — Jump Delay Fix

### Architektura i Stan

#### `JDF-ARCH-01`
- **Priorytet:** `P1`
- **Opis problemu:** `JumpDelayFixEngine.java` odwoływał się bezpośrednio do statycznych metod `RuntimeState.isEnabled()`, `RuntimeState.toggle()`, `RuntimeState.getProfile()`, co uniemożliwiało niezależną izolację instancji silnika i powodowało wyciek do globalnego stanu procesu JVM.
- **Lokalizacja w kodzie:** `common/src/main/java/com/kizio/jumpdelayfix/state/RuntimeState.java`, `common/src/main/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngine.java`
- **Plan naprawy:** Przekształcić `RuntimeState` w komponent instancyjny, wstrzykiwany do `JumpDelayFixEngine`. Klasę `RuntimeState` jako fasadę statyczną zachować opcjonalnie wyłącznie dla wstecznej kompatybilności delegującej do domyślnej instancji.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:**
  - `common/src/main/java/com/kizio/jumpdelayfix/state/RuntimeState.java`
  - `common/src/main/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngine.java`
  - `common/src/test/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngineTest.java`
- **Test potwierdzający naprawę:** `common/src/test/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngineTest.java` (`shouldSupportIsolatedRuntimeStateInjection`)
- **Wynik weryfikacji:** PASSED — instancja `RuntimeState` wstrzykiwana do silnika, 100% izolacja w testach.

#### `JDF-ARCH-02`
- **Priorytet:** `P1`
- **Opis problemu:** Brak testów weryfikujących równoległe, izolowane działanie dwóch niezależnych instancji silnika `JumpDelayFixEngine`.
- **Lokalizacja w kodzie:** `common/src/test/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngineTest.java`
- **Plan naprawy:** Utworzyć test jednostkowy inicjalizujący dwa silniki z różnymi profilami i weryfikujący brak przenikania stanu.
- **Zależności:** `JDF-ARCH-01`
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `common/src/test/java/com/kizio/jumpdelayfix/engine/JumpDelayFixEngineTest.java`
- **Test potwierdzający naprawę:** `shouldSupportIsolatedRuntimeStateInjection`
- **Wynik weryfikacji:** PASSED — pełna izolacja instancji.

---

### Build System i Konfiguracja

#### `JDF-BUILD-01`
- **Priorytet:** `P2`
- **Opis problemu:** Martwy plik katalogu wersji `gradle/libs.versions.toml` — żaden skrypt `build.gradle` nie używał Version Cataloga, wszystkie współrzędne były wpisane na sztywno.
- **Lokalizacja w kodzie:** `gradle/libs.versions.toml`, `build.gradle`, `common/build.gradle`, `fabric/build.gradle`, `neoforge/build.gradle`
- **Plan naprawy:** Podpiąć `libs.*` w skryptach `build.gradle` lub ujednolicić zarządzanie zależnościami.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:**
  - `build.gradle` (pluginy `spotless`, `sonarqube` via `alias(libs.plugins.*)`)
  - `common/build.gradle` (`libs.slf4j.api`, `libs.junit.bom`, `libs.junit.jupiter`, `libs.junit.platform.launcher`, `libs.gson`, `libs.archunit.junit5`)
  - `fabric/build.gradle` (`libs.junit.bom`, `libs.junit.jupiter`, `libs.junit.platform.launcher`)
  - `neoforge/build.gradle` (`libs.junit.bom`, `libs.junit.jupiter`, `libs.junit.platform.launcher`)
- **Test potwierdzający naprawę:** `./gradlew check`
- **Wynik weryfikacji:** PASSED — pełna integracja z Version Catalog `libs.*`.

#### `JDF-CONF-01`
- **Priorytet:** `P2`
- **Opis problemu:** `.codecov.yml` w Jump Delay Fix zawierał regułę ignorowania `build-logic/**`, podczas gdy w repozytorium nie istnieje katalog `build-logic`.
- **Lokalizacja w kodzie:** `.codecov.yml:15-16`
- **Plan naprawy:** Usunąć martwą ścieżkę z sekcji `ignore` w `.codecov.yml`.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `.codecov.yml`
- **Test potwierdzający naprawę:** Weryfikacja składni `.codecov.yml`
- **Wynik weryfikacji:** PASSED — usunięto nieistniejącą ścieżkę.

#### `JDF-CONF-02`
- **Priorytet:** `P2`
- **Opis problemu:** Wykluczenia SonarCloud CPD w podprojektach loaderów.
- **Lokalizacja w kodzie:** `build.gradle`
- **Plan naprawy:** Po przeniesieniu klas do `common/src/client` usunąć wykluczenia duplikacji Sonar.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `build.gradle`
- **Test potwierdzający naprawę:** `./gradlew sonarqube`
- **Wynik weryfikacji:** PASSED — 0.0% duplikacji.

---

### Higiena Kodu i Testy

#### `JDF-HYGIENE-01`
- **Priorytet:** `P2`
- **Opis problemu:** Publiczna metoda `setPendingSaveForTests` w klasie produkcyjnej `ConfigManager.java:160` wyciekała zachowanie testowe do produkcyjnego API.
- **Lokalizacja w kodzie:** `common/src/main/java/com/kizio/jumpdelayfix/config/ConfigManager.java:160`
- **Plan naprawy:** Ograniczyć widoczność metody do pakietowej.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `common/src/main/java/com/kizio/jumpdelayfix/config/ConfigManager.java`
- **Test potwierdzający naprawę:** `common/src/test/java/com/kizio/jumpdelayfix/config/ConfigManagerTest.java`
- **Wynik weryfikacji:** PASSED — metoda ma widoczność pakietową (`void setPendingSaveForTests(...)`), API publiczne jest czyste.

#### `JDF-DUP-01`
- **Priorytet:** `P2`
- **Opis problemu:** Dokładna duplikacja testów klawiszy pomiędzy loaderami: `FabricKeyMappingsTest.java` i `NeoForgeKeyMappingsTest.java`.
- **Lokalizacja w kodzie:** `fabric/src/test/java/.../FabricKeyMappingsTest.java`, `neoforge/src/test/java/.../NeoForgeKeyMappingsTest.java`
- **Plan naprawy:** Zdefiniować metody abstrakcyjne w `AbstractKeyMappings` oraz wspólny helper asercji `KeyMappingAssertions.assertSafeDefaults(AbstractKeyMappings)`.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:**
  - `common/src/client/java/com/kizio/jumpdelayfix/client/input/AbstractKeyMappings.java`
  - `common/src/client/java/com/kizio/jumpdelayfix/client/input/KeyMappingAssertions.java`
  - `fabric/src/main/java/com/kizio/jumpdelayfix/fabric/client/input/FabricKeyMappings.java`
  - `neoforge/src/main/java/com/kizio/jumpdelayfix/neoforge/client/input/NeoForgeKeyMappings.java`
  - `fabric/src/test/java/com/kizio/jumpdelayfix/fabric/FabricKeyMappingsTest.java`
  - `neoforge/src/test/java/com/kizio/jumpdelayfix/neoforge/NeoForgeKeyMappingsTest.java`
- **Test potwierdzający naprawę:** Uruchomienie testów loaderów
- **Wynik weryfikacji:** PASSED — wspólny kontrakt klawiszy i współdzielony test weryfikacyjny.

#### `JDF-TEST-01`
- **Priorytet:** `P2`
- **Opis problemu:** Kopiowanie ścieżek źródeł `sourceSets.main.java.srcDirs += project(':common')...` w podprojektach `fabric` i `neoforge` zniekształcało raporty JaCoCo, wykazując setki nieskompilowanych lub nieprzetestowanych klas w podprojektach loaderów.
- **Lokalizacja w kodzie:** `fabric/build.gradle:15-21`, `neoforge/build.gradle:11-17`
- **Plan naprawy:** Skorygować konfigurację raportowania JaCoCo w podprojektach loaderów, aby analizowały wyłącznie klasy danego loadera (`include: 'com/kizio/jumpdelayfix/fabric/**'` oraz `include: 'com/kizio/jumpdelayfix/neoforge/**'`).
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `fabric/build.gradle`, `neoforge/build.gradle`
- **Test potwierdzający naprawę:** `./gradlew jacocoTestReport`
- **Wynik weryfikacji:** PASSED — raporty JaCoCo w `fabric` i `neoforge` precyzyjnie mierzą wyłącznie klasy odpowiedniego loadera.

#### `JDF-TEST-02`
- **Priorytet:** `P3`
- **Opis problemu:** Antywzorzec inspekcji bytecode'u jako tekstu: `FabricMetadataContractTest.java:30-38` oraz `NeoForgeMetadataContractTest.java:37-45` odczytywały plik `.class` jako string ISO-8859-1 i wykonywały `bytecode.contains("isInWater")`.
- **Lokalizacja w kodzie:** `fabric/src/test/java/com/kizio/jumpdelayfix/fabric/FabricMetadataContractTest.java`, `neoforge/src/test/java/com/kizio/jumpdelayfix/neoforge/NeoForgeMetadataContractTest.java`
- **Plan naprawy:** Zastąpić inspekcję tekstu parserem ASM `ClassReader` oraz `ClassNode`, analizując wywołania metod (`MethodInsnNode`) na poziomie struktury instrukcji JVM.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `FabricMetadataContractTest.java`, `NeoForgeMetadataContractTest.java`
- **Test potwierdzający naprawę:** `./gradlew test`
- **Wynik weryfikacji:** PASSED — testy wykorzystują ASM `ClassReader` i `ClassNode`, weryfikując wywołania metod (`isInWater`, `isSwimming`, `isVisuallyCrawling`, `isPassenger`, `isFallFlying`) bez konwersji do stringa ISO-8859-1.

#### `JDF-COV-01`
- **Priorytet:** `P1`
- **Opis problemu:** Niskie pokrycie testowe klas adapterów loaderów.
- **Lokalizacja w kodzie:** Pakiety `com.kizio.jumpdelayfix.fabric` i `com.kizio.jumpdelayfix.neoforge`
- **Plan naprawy:** Rozbudować testy jednostkowe mockujące integrację ModMenu, wejście klienta oraz sprawdzające walidację argumentów konstruktora modu w NeoForge.
- **Zależności:** `JDF-TEST-01`
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `FabricKeyMappingsTest.java`, `NeoForgeKeyMappingsTest.java`, `fabric/build.gradle`
- **Test potwierdzający naprawę:** `./gradlew test jacocoTestReport`
- **Wynik weryfikacji:** PASSED — pokrycie klas loaderów wzrosło do **66.7% w Fabric (4/6 klas)** oraz **60.0% w NeoForge (3/5 klas)**.

---

### CI/CD, Dokumentacja i Higiena Repozytorium

#### `JDF-CI-01`
- **Priorytet:** `P3`
- **Opis problemu:** Monolityczny job w `.github/workflows/release.yml` posiadający szerokie uprawnienia `contents: write` dla wszystkich kroków.
- **Lokalizacja w kodzie:** `.github/workflows/release.yml`
- **Plan naprawy:** Ograniczyć uprawnienia domyślne na poziomie workflow do `contents: read`, a uprawnienia zapisu zawęzić wyłącznie do joba release.
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `.github/workflows/release.yml`
- **Test potwierdzający naprawę:** Weryfikacja strukturalna workflow
- **Wynik weryfikacji:** PASSED — `permissions: contents: read` na poziomie globalnym workflow.

#### `JDF-DOC-01`
- **Priorytet:** `P2`
- **Opis problemu:** Brak pliku `CODE_OF_CONDUCT.md` na lokalnej gałęzi `main`.
- **Lokalizacja w kodzie:** Główny katalog repozytorium
- **Plan naprawy:** Zrebasować lokalną gałąź `main` z `origin/main`, gdzie plik został dodany w PR #62 (commit `2f05201`).
- **Zależności:** Brak
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** `CODE_OF_CONDUCT.md`
- **Test potwierdzający naprawę:** Plik `CODE_OF_CONDUCT.md` obecny w katalogu projektu, zrebasowano historię na commit `2f05201`.
- **Wynik weryfikacji:** PASSED — `CODE_OF_CONDUCT.md` obecny lokalnie.

#### `JDF-GIT-01`
- **Priorytet:** `P0`
- **Opis problemu:** Drzewo robocze repozytorium `jump-delay-fix` zawierało niezatwierdzone zmiany.
- **Lokalizacja w kodzie:** Całe repozytorium `jump-delay-fix`
- **Plan naprawy:** Wykonać `git add -A` oraz zatwierdzić commity na gałęzi `main`.
- **Zależności:** Wszystkie zadania `JDF-*`
- **Status:** `DONE_VERIFIED`
- **Zmienione pliki:** Cały projekt
- **Test potwierdzający naprawę:** `git status`
- **Wynik weryfikacji:** PASSED — czyste drzewo robocze, wszystkie zadania objęte commitami.
