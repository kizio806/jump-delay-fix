package com.kizio.jumpdelayfix;

import com.kizio.jumpdelayfix.config.ConfigStorage;
import com.kizio.jumpdelayfix.config.RuntimeConfig;
import com.kizio.jumpdelayfix.model.JumpProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);
    private static final int CONFIG_SAVE_DEBOUNCE_TICKS = 10;
    private static final int MAX_TRACKED_SERVERS = 128;
    private static final ExecutorService CONFIG_IO = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, Constants.MOD_ID + "-config-io");
        thread.setDaemon(true);
        return thread;
    });

    private RuntimeConfig runtimeConfig = RuntimeConfig.defaults();
    private final Map<String, JumpProfile> serverProfiles = createServerScopedLruMap();
    private Path configFilePath = Path.of("config", "jumpdelayfix.properties");
    private int saveCooldownTicks;
    private boolean dirty;
    private CompletableFuture<Void> pendingSave = CompletableFuture.completedFuture(null);

    void load(Path configDirectory) {
        awaitPendingSaves();
        configFilePath = Objects.requireNonNull(configDirectory, "configDirectory").resolve("jumpdelayfix.properties");

        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(configFilePath);
        runtimeConfig = loadedConfig.config().copy();

        serverProfiles.clear();
        serverProfiles.putAll(loadedConfig.serverProfiles());

        saveCooldownTicks = 0;
        dirty = false;
    }

    boolean autoProfileSwitch() {
        return runtimeConfig.autoProfileSwitch();
    }

    void setAutoProfileSwitch(boolean enabled) {
        if (runtimeConfig.autoProfileSwitch() == enabled) {
            return;
        }

        runtimeConfig.setAutoProfileSwitch(enabled);
        markDirty();
    }

    JumpProfile resolveProfile(String serverId) {
        return serverProfiles.getOrDefault(serverId, JumpProfile.SMART);
    }

    void rememberProfile(String serverId, JumpProfile profile) {
        serverProfiles.put(Objects.requireNonNull(serverId, "serverId"), Objects.requireNonNull(profile, "profile"));
        markDirty();
    }

    void flushIfDue() {
        if (!dirty) {
            return;
        }

        if (saveCooldownTicks > 0) {
            saveCooldownTicks--;
            return;
        }

        persistAsync();
        dirty = false;
    }

    void flushNow() {
        awaitPendingSaves();
        if (!dirty) {
            return;
        }

        ConfigStorage.save(configFilePath, runtimeConfig, serverProfiles);
        dirty = false;
        saveCooldownTicks = 0;
    }

    void resetForTests() {
        awaitPendingSaves();
        runtimeConfig = RuntimeConfig.defaults();
        serverProfiles.clear();
        configFilePath = Path.of("config", "jumpdelayfix.properties");
        saveCooldownTicks = 0;
        dirty = false;
    }

    private void markDirty() {
        dirty = true;
        saveCooldownTicks = CONFIG_SAVE_DEBOUNCE_TICKS;
    }

    private void persistAsync() {
        Path filePathSnapshot = configFilePath;
        RuntimeConfig configSnapshot = runtimeConfig.copy();
        Map<String, JumpProfile> serverProfilesSnapshot = new LinkedHashMap<>(serverProfiles);

        pendingSave = pendingSave.exceptionally(exception -> null)
                .thenRunAsync(() -> ConfigStorage.save(filePathSnapshot, configSnapshot, serverProfilesSnapshot), CONFIG_IO);
    }

    private void awaitPendingSaves() {
        try {
            pendingSave.join();
        } catch (RuntimeException exception) {
            LOGGER.warn("Asynchronous configuration persistence failed", exception);
        } finally {
            pendingSave = CompletableFuture.completedFuture(null);
        }
    }

    private static <V> Map<String, V> createServerScopedLruMap() {
        return new LinkedHashMap<>(16, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                return size() > MAX_TRACKED_SERVERS;
            }
        };
    }
}
