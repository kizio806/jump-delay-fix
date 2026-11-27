package com.kizio.jumpdelayfix.config;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.util.BoundedLruMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);
    private static final int CONFIG_SAVE_DEBOUNCE_TICKS = 10;
    private static final int MAX_TRACKED_SERVERS = 128;

    private final ExecutorService configIo;
    private RuntimeConfig runtimeConfig = RuntimeConfig.defaults();
    private final Map<String, JumpProfile> serverProfiles = BoundedLruMap.create(MAX_TRACKED_SERVERS);
    private Path configFilePath = Path.of("config", "jumpdelayfix.properties");
    private int saveCooldownTicks;
    private boolean dirty;
    private CompletableFuture<Void> pendingSave = CompletableFuture.completedFuture(null);

    public ConfigManager() {
        this(Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, Constants.MOD_ID + "-config-io");
            thread.setDaemon(true);
            return thread;
        }));
    }

    public ConfigManager(ExecutorService configIo) {
        this.configIo = Objects.requireNonNull(configIo, "configIo");
    }

    public synchronized void load(Path configDirectory) {
        awaitPendingSaves();
        configFilePath = Objects.requireNonNull(configDirectory, "configDirectory").resolve("jumpdelayfix.properties");

        ConfigStorage.LoadedConfig loadedConfig = ConfigStorage.load(configFilePath);
        runtimeConfig = loadedConfig.config().copy();

        synchronized (serverProfiles) {
            serverProfiles.clear();
            serverProfiles.putAll(loadedConfig.serverProfiles());
        }

        saveCooldownTicks = 0;
        dirty = false;
    }

    public synchronized boolean autoProfileSwitch() {
        return runtimeConfig.autoProfileSwitch();
    }

    public synchronized void setAutoProfileSwitch(boolean enabled) {
        if (runtimeConfig.autoProfileSwitch() == enabled) {
            return;
        }

        runtimeConfig.setAutoProfileSwitch(enabled);
        markDirty();
    }

    public JumpProfile resolveProfile(String serverId) {
        synchronized (serverProfiles) {
            return serverProfiles.getOrDefault(serverId, JumpProfile.SMART);
        }
    }

    public void rememberProfile(String serverId, JumpProfile profile) {
        synchronized (serverProfiles) {
            serverProfiles.put(Objects.requireNonNull(serverId, "serverId"), Objects.requireNonNull(profile, "profile"));
        }
        markDirty();
    }

    public synchronized void flushIfDue() {
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

    public synchronized void flushNow() {
        awaitPendingSaves();
        if (!dirty) {
            return;
        }

        Map<String, JumpProfile> serverProfilesSnapshot;
        synchronized (serverProfiles) {
            serverProfilesSnapshot = new LinkedHashMap<>(serverProfiles);
        }

        ConfigStorage.save(configFilePath, runtimeConfig, serverProfilesSnapshot);
        dirty = false;
        saveCooldownTicks = 0;
    }

    public synchronized void resetToDefaults() {
        awaitPendingSaves();
        runtimeConfig = RuntimeConfig.defaults();
        synchronized (serverProfiles) {
            serverProfiles.clear();
        }
        dirty = true;
        saveCooldownTicks = 0;
        flushNow();
    }

    public synchronized void resetForTests() {
        awaitPendingSaves();
        runtimeConfig = RuntimeConfig.defaults();
        synchronized (serverProfiles) {
            serverProfiles.clear();
        }
        configFilePath = Path.of("config", "jumpdelayfix.properties");
        saveCooldownTicks = 0;
        dirty = false;
    }

    public synchronized void shutdown() {
        awaitPendingSaves();
        configIo.shutdown();
        try {
            if (!configIo.awaitTermination(2, TimeUnit.SECONDS)) {
                configIo.shutdownNow();
            }
        } catch (InterruptedException e) {
            configIo.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private synchronized void markDirty() {
        dirty = true;
        saveCooldownTicks = CONFIG_SAVE_DEBOUNCE_TICKS;
    }

    private synchronized void persistAsync() {
        Path filePathSnapshot = configFilePath;
        RuntimeConfig configSnapshot = runtimeConfig.copy();
        Map<String, JumpProfile> serverProfilesSnapshot;
        synchronized (serverProfiles) {
            serverProfilesSnapshot = new LinkedHashMap<>(serverProfiles);
        }

        pendingSave = pendingSave.exceptionally(exception -> null)
                .thenRunAsync(() -> ConfigStorage.save(filePathSnapshot, configSnapshot, serverProfilesSnapshot), configIo);
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

    void setPendingSaveForTests(CompletableFuture<Void> pendingSave) {
        this.pendingSave = pendingSave;
    }
}
