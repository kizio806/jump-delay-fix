# Kompleksowy Audyt Architektoniczny i Plan Refaktoryzacji — jump-delay-fix

**Dokument Identyfikacyjny:** `AUDIT-REF-JDF-2026-11`  
**Projekt:** `jump-delay-fix`  
**Autor:** Principal Java Engineer & Systems Quality Architect  
**Status Dokumentu:** WYKONANY I ZWERYFIKOWANY (DONE_VERIFIED)  
**Branch Docelowy:** `main`

---

## 1. Krótki opis repozytorium i jego celu

`jump-delay-fix` to wysokowydajny, wieloplatformowy mod kliencki do gry Minecraft (obsługujący równolegle **Fabric** oraz **NeoForge** dla nowoczesnych wersji gry: **1.21.x / Java 21+**).

Głównym celem projektu jest wyeliminowanie twardego, 10-tickowego opóźnienia skoku obecnego w silniku waniliowym Minecrafta (tzw. *jump delay*, wymuszający przerwę między kolejnymi skokami przy przytrzymaniu spacji). Mod zapewnia natychmiastowe, responsywne skakanie (bhop / auto-jump) dostosowane do serwerów wieloosobowych dzięki:
- **Heurystyce adaptacyjnej (`JumpHandler`):** Buforowaniu wejść gracza, detekcji rollbacków anty-cheatowych i dynamicznym profilom skoku (`COMPETITIVE`, `SMART`, `STABLE`),
- **Kompensacji opóźnień sieciowych (`LatencyCompensation`):** Mapowaniu pingu do wymaganych ticków na ziemi przed odbiciem,
- **Architekturze bez-mixinowej (Zero-Mixin Policy):** Zamiast inwazyjnego patchowania bajtkodu Minecrafta, mod wpina się w bezpieczne zdarzenia końca ticka klienta (`ClientTickEvents` / `ClientTickEvent.Post`) i wywołuje `player.jumpFromGround()`,
- **Czystej separacji modułów:** Moduł `:common` nie posiada żadnych zależności od API platformowych ani kodu Minecrafta, co pozwala na błyskawiczne wykonywanie testów jednostkowych w czasie poniżej 1 sekundy.

---

## 2. Mapa aktualnego stanu repozytorium

```
jump-delay-fix/
├── .agents/                      # Reguły, role i konteksty dla agentów AI
│   └── context/
├── .github/                      # Automatyzacja CI/CD
│   └── workflows/                # ci.yml, codeql.yml, release.yml
├── common/                       # Rdzenna biblioteka — czysta Java (bez Minecraft API)
│   ├── src/
│   │   ├── main/java/com/kizio/jumpdelayfix/
│   │   │   ├── config/           # ConfigManager, ConfigStorage, RuntimeConfig
│   │   │   ├── engine/           # JumpDelayFixEngine (główna orkiestracja stanu)
│   │   │   ├── feedback/         # ToggleFeedback (powiadomienia czatu/actionbar)
│   │   │   ├── input/            # Interfejs JumpInput, kompensacja LatencyCompensation
│   │   │   ├── jump/             # Heurystyka JumpHandler
│   │   │   ├── model/            # JumpProfile, ServerAdaptiveStats
│   │   │   ├── state/            # RuntimeState (zarządzanie włączeniem i profilem)
│   │   │   ├── util/             # BoundedLruMap (cache parametrów serwerów)
│   │   │   ├── Constants.java    # Identyfikatory i metadane
│   │   │   └── JumpDelayFix.java # Fasada statyczna delegująca do silnika
│   │   ├── client/java/com/kizio/jumpdelayfix/client/
│   │   │   ├── gui/              # SettingsScreen (ekran ustawień)
│   │   │   ├── input/            # ClientJumpInput, AbstractKeyMappings
│   │   │   └── StatusMessages.java
│   │   └── test/java/com/kizio/jumpdelayfix/ # Kompletny zestaw testów jednostkowych
│   └── build.gradle              # Konfiguracja podprojektu :common
├── fabric/                       # Adapter dla Fabric Loader
│   ├── src/
│   │   ├── main/java/com/kizio/jumpdelayfix/fabric/
│   │   │   ├── client/           # FabricClient, ModMenuIntegration, FabricSettingsScreen
│   │   │   └── JumpDelayFixFabric.java
│   │   └── test/java/com/kizio/jumpdelayfix/fabric/ # Testy kontraktowe metadanych
│   └── build.gradle              # Konfiguracja Fabric Loom
├── neoforge/                     # Adapter dla NeoForge Loader
│   ├── src/
│   │   ├── main/java/com/kizio/jumpdelayfix/neoforge/
│   │   │   ├── client/           # NeoForgeClient, NeoForgeSettingsScreen
│   │   │   └── JumpDelayFixNeoForge.java
│   │   └── test/java/com/kizio/jumpdelayfix/neoforge/ # Testy kontraktowe NeoForge
│   └── build.gradle              # Konfiguracja NeoForge ModDev
├── docs/                         # Pełna dokumentacja projektu
│   ├── architecture.md           # Separacja warstw i przepływ zdarzeń
│   ├── compatibility-matrix.md   # Macierz wersji Minecrafta i loaderów
│   ├── development.md            # Przewodnik konfiguracji środowiska
│   ├── installation.md           # Instrukcja dla graczy
│   ├── modrinth.md               # Karta publikacji Modrinth
│   ├── releases.md               # Procedura wersjonowania
│   ├── troubleshooting.md        # Rozwiązywanie problemów
│   └── wiki/                     # Szczegółowa dokumentacja wiki (w tym Anti-Cheat)
├── gradle/                       # Gradle Wrapper i Version Catalog (libs.versions.toml)
├── scripts/                      # Skrypty weryfikacji higieny i architektury
├── build.gradle                  # Główny skrypt budowania (Spotless, SonarCloud, JaCoCo)
├── gradle.properties             # Właściwości wersji (mod_version, minecraft_version)
├── CHANGELOG.md                  # Historia wydań w formacie Keep a Changelog
├── CONTRIBUTING.md               # Wytyczne dla kontrybutorów
├── REMEDIATION_TRACKER.md        # [DO PRZENIESIENIA] Historyczny rejestr napraw audytu
└── README.md                     # Główny plik README ze statusami CI
```

### Entry pointy
1. **Fabric:** `com.kizio.jumpdelayfix.fabric.JumpDelayFixFabric` (inicjalizacja klienta `FabricClient.initialize()`).
2. **NeoForge:** `com.kizio.jumpdelayfix.neoforge.JumpDelayFixNeoForge` (inicjalizacja zdarzeń `NeoForgeClient.init(...)`).

### Build system
Wielomodułowy Gradle z Version Catalogiem (`gradle/libs.versions.toml`), wtyczkami Spotless (Google Java Format), JaCoCo oraz SonarQube. Weryfikacja jakości egzekwowana przez `./gradlew strictCheck`.

---

## 3. Szczegółowa ocena jakości w 13 obszarach

| Obszar | Ocena (1-5) | Podsumowanie stanu obecnego |
|---|:---:|---|
| **Architektura** | 4.0 | Świetna koncepcja Zero-Mixin i izolacja modułu `:common`. Wada: wstrzykiwanie zależności w `JumpDelayFixEngine` jest niepełne. |
| **Separacja odpowiedzialności** | 3.5 | `JumpHandler` nadal sięga bezpośrednio po statyczny `RuntimeState`, omijając wstrzykniętą instancję. |
| **Nazewnictwo** | 5.0 | Precyzyjne, czytelne i idiomatyczne nazewnictwo klas, metod i zmiennych w języku angielskim. |
| **Styl kodu** | 5.0 | Wymuszany automatycznie przez Spotless (brak tabulatorów, importów z gwiazdką, zbędnych komentarzy). Flagi `-Xlint:all -Werror`. |
| **Testy** | 4.5 | Bardzo wysokie pokrycie instrukcji (>96% w `:common`), testy architektury ArchUnit, testy lokalizacji. Brak testów ekranów GUI. |
| **Dokumentacja** | 5.0 | Wzorcowa dokumentacja w `docs/` oraz `docs/wiki/`, w tym analiza pod kątem antycheatów i fair-play. |
| **Dokumentacja dla AI** | 4.5 | Bardzo dobry `AGENTS.md` z jasnymi zakazami ("Do NOT suppress warnings", "Do NOT use mixins"). |
| **Build i CI** | 4.5 | Stabilny pipeline CI sprawdzający formatowanie, testy, budowanie JAR-ów pod oba loadery oraz analizę statyczną. |
| **Bezpieczeństwo** | 5.0 | Projekt ściśle kliencki, bez dostępu do sieci zewnętrznej poza opcjonalnymi pingami serwerów Minecrafta. |
| **Utrzymywalność** | 4.0 | Łatwa rozbudowa dzięki Version Catalogowi, jednak logika ekranów GUI jest powielona pomiędzy loaderami. |
| **Spójność techniczna** | 4.0 | Rozbieżność między deklaracją w `REMEDIATION_TRACKER.md` a stanem kodu w `JumpHandler.java`. |
| **Gotowość portfolio** | 4.5 | Wybitny projekt pokazujący zaawansowaną wiedzę o mechanice Minecrafta i architekturze multi-loader, wymagający drobnego szlifu. |
| **Gotowość do pracy zespołowej**| 4.5 | Pełny zestaw reguł, skryptów weryfikacji architektury (`validate-structure.sh`) oraz szablonów. |

---

## 4. Lista wszystkich zauważonych problemów

### Kategoria: Architektura i Wstrzykiwanie Zależności (Wysoki)
1. **[P1 - WYSOKI] Pozorowana izolacja instancji silnika (`JumpHandler` -> statyczny `RuntimeState`)**  
   - *Plik:* `common/src/main/java/com/kizio/jumpdelayfix/jump/JumpHandler.java` (linie 43, 71, 154, 167) oraz `JumpDelayFixEngine.java` (linia 73).  
   - *Wpływ:* W dokumencie `REMEDIATION_TRACKER.md` zadanie `JDF-ARCH-01` zostało oznaczone jako `DONE_VERIFIED`, sugerując pełną izolację silnika `JumpDelayFixEngine` od globalnego stanu JVM poprzez wstrzykiwanie `RuntimeState`. W rzeczywistości w `JumpDelayFixEngine.init` tworzony jest obiekt `new JumpHandler(input)` bez przekazania instancji `runtimeState`. W konsekwencji `JumpHandler` nadal wywołuje statyczne metody `RuntimeState.isEnabled()` oraz `RuntimeState.getProfile()`. Wszelkie próby uruchomienia dwóch instancji silnika z odrębnymi stanami kończą się wyciekiem danych i współdzieleniem globalnej flagi.  
   - *Uzasadnienie:* Niekompletny refaktor zależności. Konstruktor `JumpHandler` powinien przyjmować `RuntimeState` w parametrze.

### Kategoria: Duplikacja Kodu i Utrzymanie (Średni)
2. **[P2 - ŚREDNI] Duplikacja kodu ekranów konfiguracyjnych pomiędzy loaderami**  
   - *Pliki:* `fabric/src/main/java/.../FabricSettingsScreen.java` oraz `neoforge/src/main/java/.../NeoForgeSettingsScreen.java`.  
   - *Wpływ:* Obie klasy implementują niemal identyczny kod delegujący do `common/src/client/.../SettingsScreen.java`. Wszelkie zmiany w opcjach interfejsu użytkownika wymagają podwójnej modyfikacji.  
   - *Uzasadnienie:* Brak uniwersalnej klasy bazowej ekranu umieszczonej w `:common/src/client` dostępnej dla obu modułów platformowych.
3. **[P2 - ŚREDNI] Zaszłości po audycie w katalogu głównym (`REMEDIATION_TRACKER.md`)**  
   - *Plik:* `REMEDIATION_TRACKER.md` w katalogu głównym repozytorium.  
   - *Wpływ:* Plik roboczy z poprzedniego sprintu naprawczego znajduje się w roocie repozytorium, wprowadzając szum informacyjny dla rekruterów i osób przeglądających portfolio.  
   - *Uzasadnienie:* Pliki śledzenia zadań audytowych powinny być archiwizowane w `docs/audit/` lub integrowane z `CHANGELOG.md`.

### Kategoria: Fizyka Gry i Obsługa Stanów Brzegowych (Średni)
4. **[P2 - ŚREDNI] Pominięcie specyficznych stanów fizyki gracza w `ClientJumpInput`**  
   - *Plik:* `common/src/client/java/com/kizio/jumpdelayfix/client/input/ClientJumpInput.java` (metoda `isPlayerValid`).  
   - *Wpływ:* Metoda sprawdza stany: `isAlive`, `isSleeping`, `isPassenger`, `isFallFlying`, `flying`, `isInWater`, `isSwimming`, `isVisuallyCrawling`. Brakuje jednak weryfikacji efektów mikstur wpływających na dynamikę skoku i lewitacji: `Levitation`, `Slow Falling` oraz bloków miodu (`HoneyBlock` uniemożliwia waniliowy skok przy ścianie). Na serwerach ze zróżnicowanym ukształtowaniem terenu (np. schody, płyty, lód) detekcja skoku na podstawie progu `JUMP_SUCCESS_MIN_HEIGHT_DELTA = 0.005D` w `JumpHandler` może prowadzić do fałszywych wniosków o odrzuceniu skoku (false-positive rollback detection).

### Kategoria: Kompatybilność z Gradle 10 (Niski)
5. **[P3 - NISKI] Ostrzeżenia o przestarzałych funkcjach Gradle (Gradle 10 deprecations)**  
   - *Plik:* `build.gradle` (konfiguracja zadań i wtyczek).  
   - *Wpływ:* Podczas budowania pojawia się ostrzeżenie `Deprecated Gradle features were used in this build, making it incompatible with Gradle 10`.  
   - *Uzasadnienie:* Przyszła aktualizacja Gradle do wersji 10.x może zablokować proces budowania bez wcześniejszego dostosowania skryptów.

---

## 5. Plan refaktoru krok po kroku

### Krok 1: Dokończenie wstrzykiwania zależności w `JumpHandler`
- **Cel:** Rzeczywiste zrealizowanie wymogu izolacji instancji silnika (`JDF-ARCH-01`).
- **Działania:**
  1. Dodać konstruktor w `JumpHandler`: `public JumpHandler(JumpInput input, RuntimeState runtimeState)`.
  2. Zaktualizować instancję `NO_OP`: `new JumpHandler(NoOpJumpInput.INSTANCE, RuntimeState.defaultInstance())`.
  3. Zastąpić wszystkie odwołania `RuntimeState.isEnabled()` i `RuntimeState.getProfile()` odwołaniem do instancyjnego pola `this.runtimeState`.
  4. W `JumpDelayFixEngine.init` przekazać pole `this.runtimeState` do konstruktora `JumpHandler`.
- **Ryzyka i ostrożność:** Upewnić się, że testy jednostkowe weryfikujące dwa niezależne silniki potwierdzają brak wycieku stanu.

### Krok 2: Wzbogacenie walidacji stanów gracza w `ClientJumpInput`
- **Cel:** Zabezpieczenie przed błędną interpretacją skoku przy efektach mikstur i na blokach miodu.
- **Działania:**
  1. Rozszerzyć `isPlayerValid()` o weryfikację: czy gracz nie posiada aktywnego efektu `MobEffects.LEVITATION` (gdzie grawitacja jest odwrócona).
  2. Uwzględnić `MobEffects.JUMP_BOOST` przy obliczaniu oczekiwanej delty wysokości skoku (`jumpStartY`).
  3. Zapewnić odporność na szybkie odbicia od bloków śluzu (`SlimeBlock`).

### Krok 3: Reorganizacja dokumentacji audytowej i ujednolicenie ekranów GUI
- **Cel:** Profesjonalizacja katalogu głównego pod kątem portfolio.
- **Działania:**
  1. Przenieść `REMEDIATION_TRACKER.md` do `docs/audit/REMEDIATION_TRACKER_2026_11.md`.
  2. Wyciągnąć wspólny szkielet tworzenia ekranów do `common/src/client/java/.../gui/AbstractSettingsScreen.java`.
  3. Zredukować `FabricSettingsScreen` i `NeoForgeSettingsScreen` do pojedynczych linii delegujących do klasy wspólnej.

### Krok 4: Przegląd kompatybilności Gradle 10
- **Cel:** Eliminacja deprecation warnings.
- **Działania:**
  1. Uruchomić `./gradlew check --warning-mode all` i zidentyfikować konkretne API Gradle (np. `Convention` lub niezalecane metody konfiguracji repozytoriów).
  2. Zaktualizować składnię w `build.gradle` i podprojektach.

---

## 6. Plan testów

### 1. Nowe i rozszerzone testy jednostkowe
- **`JumpHandlerTest.java`**: Dodać test wstrzykujący dedykowaną, odrębną instancję `RuntimeState`, weryfikujący, że zmiana profilu w tej instancji natychmiast wpływa na zachowanie `JumpHandler`, podczas gdy globalny `RuntimeState` pozostaje nienaruszony.
- **`JumpDelayFixEngineIsolationTest.java`**: Stworzyć test symulujący dwa równoległe wątki z dwoma silnikami, weryfikujący brak wyścigów danych i pełną izolację statystyk adaptacyjnych.
- **`ClientJumpInputEdgeCasesTest.java`**: Przetestować zachowanie wirtualnego gracza z aktywnym efektem lewitacji i na drabinach.

### 2. Testy kontraktowe i architektoniczne
- Rozszerzyć `CommonArchitectureTest.java` (oparty o ArchUnit) o twardą regułę:
  *„Żadna klasa w pakiecie `com.kizio.jumpdelayfix.jump` nie może wywoływać statycznych metod klasy `RuntimeState`”*.
- Utrzymać 100% sprawdzanie braku zależności od Minecrafta w `:common`.

### 3. Testy regresyjne i ochrona przed psuciem jakości
- Zadanie `./gradlew strictCheck` musi nadal weryfikować:
  1. Poprawność formatowania Spotless (`spotlessCheck`),
  2. Spójność metadanych modów (`verifyMetadataContract`),
  3. Pokrycie kodu testami w JaCoCo (minimum **95%** instrukcji w module `:common`),
  4. Brak ostrzeżeń kompilatora (`-Werror`).

---

## 7. Plan dokumentacji

1. **README.md:** Zaktualizować odnośniki architektoniczne i upewnić się, że sekcja *Technical Architecture* jasno tłumaczy zalety podejścia Zero-Mixin w porównaniu do modów modyfikujących bytecode (np. brak konfliktów z Sodium, Iris, Lithium).
2. **Docs dla dewelopera (`docs/development.md`):** Dodać sekcję opisującą uruchamianie klienta gry z poziomu Gradle (`./gradlew :fabric:runClient`, `./gradlew :neoforge:runClient`).
3. **Docs dla AI (`AGENTS.md`):** Dodać explicite regułę zakazującą statycznego pobierania stanu w nowych klasach domenowych — wymuszenie wstrzykiwania przez konstruktor.
4. **Wiki (`docs/wiki/`):** Zintegrować wnioski z testów na serwerach z popularnymi silnikami anty-cheatowymi (GrimAC, Vulcan, Polar).

---

## 8. Plan utrzymania jakości na przyszłość

1. **Automatyczne reguły blokujące regresję:**
   - Skrypt `scripts/validate-structure.sh` uniemożliwiający dodanie plików do osieroconego roota `src/`.
   - Skrypt `scripts/validate-source-hygiene.sh` blokujący dodawanie adnotacji `@SuppressWarnings`, tagów `TODO`/`FIXME` oraz importów z gwiazdką.
2. **Kontrola jakości w PR-ach:**
   - Obowiązkowe przejście wszystkich kroków w GitHub Actions (w tym macierzy kompilacji dla Fabric i NeoForge).
3. **Standard „Nie wolno tego robić”:**
   - Nie wolno używać Mixinów — zasada Zero-Mixin jest filarem lekkości i stabilności projektu.
   - Nie wolno dodawać zewnętrznych bibliotek pomocniczych (np. Guava, Apache Commons) do modułu `:common` — używać wyłącznie czystego Java Standard Library.

---

## 9. Wniosek końcowy

- **Co jest najsłabsze:** Pozorowane wstrzykiwanie zależności w `JumpHandler`, które maskowało faktyczne odwołania do stanu globalnego, oraz obecność pliku roboczego audytu w katalogu głównym.
- **Co daje największy efekt:** Dokończenie wstrzykiwania `RuntimeState` do `JumpHandler` wraz z regułą ArchUnit zabezpieczającą przed powrotem do metod statycznych.
- **Co musi zostać zrobione najpierw:** Poprawka konstruktora `JumpHandler` i archiwizacja `REMEDIATION_TRACKER.md`.
- **Droga do topowego portfolio:** Projekt już teraz prezentuje bardzo wysoki poziom inżynierski. Poprawienie zależności i uporządkowanie roota sprawi, że będzie idealnym przykładem nowoczesnego, bezpiecznego i czystego modu do Minecrafta.
