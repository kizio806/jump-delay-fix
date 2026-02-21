package com.kizio.jumpdelayfix.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationCompletenessTest {

    private static final Path LANG_DIR = Path.of("src", "main", "resources", "assets", "jumpdelayfix", "lang");
    private static final String BASE_LOCALE = "en_us.json";
    private static final Pattern ENTRY_PATTERN = Pattern.compile("^\\s*\"([^\"]+)\"\\s*:\\s*\"(.*)\"\\s*,?\\s*$");

    @Test
    void shouldContainMultipleLanguageFiles() throws IOException {
        assertTrue(Files.isDirectory(LANG_DIR), "Missing lang directory: " + LANG_DIR);

        List<String> localeFiles;
        try (Stream<Path> paths = Files.list(LANG_DIR)) {
            localeFiles = paths
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }

        assertTrue(localeFiles.contains(BASE_LOCALE), "Missing base locale file: " + BASE_LOCALE);
        assertTrue(localeFiles.size() >= 4, "Expected at least 4 locale files, found: " + localeFiles.size());
    }

    @Test
    void allLocalesShouldMatchBaseKeysAndPlaceholders() throws IOException {
        Map<String, String> baseEntries = readEntries(LANG_DIR.resolve(BASE_LOCALE));
        assertFalse(baseEntries.isEmpty(), "Base locale file has no translation entries");

        try (Stream<Path> paths = Files.list(LANG_DIR)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".json"))
                    .filter(path -> !BASE_LOCALE.equals(path.getFileName().toString()))
                    .forEach(path -> {
                        try {
                            Map<String, String> localizedEntries = readEntries(path);
                            String locale = path.getFileName().toString();

                            assertEquals(baseEntries.keySet(), localizedEntries.keySet(),
                                    "Locale keys mismatch for " + locale);

                            for (Map.Entry<String, String> baseEntry : baseEntries.entrySet()) {
                                String key = baseEntry.getKey();
                                String baseValue = baseEntry.getValue();
                                String localizedValue = localizedEntries.get(key);

                                assertEquals(
                                        countPlaceholders(baseValue),
                                        countPlaceholders(localizedValue),
                                        "Placeholder mismatch for key '" + key + "' in " + locale
                                );
                            }
                        } catch (IOException exception) {
                            throw new IllegalStateException("Failed to validate locale: " + path, exception);
                        }
                    });
        }
    }

    private static Map<String, String> readEntries(Path file) throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        for (String line : lines) {
            Matcher matcher = ENTRY_PATTERN.matcher(line);
            if (!matcher.matches()) {
                continue;
            }

            String key = matcher.group(1);
            String value = matcher.group(2);
            entries.put(key, value);
        }

        return entries;
    }

    private static int countPlaceholders(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;
        while ((index = value.indexOf("%s", index)) >= 0) {
            count++;
            index += 2;
        }

        return count;
    }
}
