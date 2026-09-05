package com.kizio.jumpdelayfix.config;

import com.kizio.jumpdelayfix.model.JumpProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class ConfigStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigStorage.class);

    private static final String KEY_SERVER_PROFILE_PREFIX = "serverProfile.";

    private ConfigStorage() {

    }
    public static LoadedConfig load(Path filePath) {
        Objects.requireNonNull(filePath, "filePath");

        RuntimeConfig config = RuntimeConfig.defaults();
        Map<String, JumpProfile> profiles = new HashMap<>();

        if (!Files.exists(filePath)) {
            return new LoadedConfig(config, profiles);
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(filePath)) {
            properties.load(input);
        } catch (IOException exception) {
            LOGGER.warn("Failed to load config from {}. Falling back to defaults.", filePath, exception);
            return new LoadedConfig(config, profiles);
        }

        config = RuntimeConfig.fromProperties(properties);

        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(KEY_SERVER_PROFILE_PREFIX)) {
                continue;
            }

            String encodedServer = key.substring(KEY_SERVER_PROFILE_PREFIX.length());
            String serverId = decodeServerId(encodedServer);
            if (serverId == null || serverId.isBlank()) {
                continue;
            }

            JumpProfile profile = parseProfile(properties.getProperty(key));
            if (profile != null) {
                profiles.put(serverId, profile);
            }
        }

        return new LoadedConfig(config, profiles);
    }
    public static void save(Path filePath, RuntimeConfig config, Map<String, JumpProfile> serverProfiles) {
        Objects.requireNonNull(filePath, "filePath");
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(serverProfiles, "serverProfiles");

        Properties properties = config.toProperties();

        serverProfiles.forEach((serverId, profile) -> {
            if (serverId != null && !serverId.isBlank() && profile != null) {
                properties.setProperty(KEY_SERVER_PROFILE_PREFIX + encodeServerId(serverId), profile.name());
            }
        });

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path targetDirectory = parent != null ? parent : Path.of(".");
            Path temporaryFile = Files.createTempFile(targetDirectory, "jumpdelayfix-", ".properties.tmp");
            try (OutputStream output = Files.newOutputStream(temporaryFile)) {
                properties.store(output, "JumpDelayFix configuration");
                try {
                    Files.move(
                            temporaryFile,
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE
                    );
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException exception) {

            LOGGER.warn("Failed to persist config to {}. Runtime state remains active.", filePath, exception);
        }
    }
    private static JumpProfile parseProfile(String value) {
        if (value == null) {
            return null;
        }

        try {
            return JumpProfile.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static String encodeServerId(String serverId) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(serverId.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeServerId(String encodedServerId) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedServerId);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
    public record LoadedConfig(RuntimeConfig config, Map<String, JumpProfile> serverProfiles) {
    }
}
