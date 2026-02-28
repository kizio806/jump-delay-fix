package com.kizio.jumpdelayfix.neoforge.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Objects;

/**
 * Registers NeoForge data generation listeners.
 */
public final class NeoForgeDataGen {

    private NeoForgeDataGen() {
    }

    /**
     * Attaches data generation listeners to the mod event bus.
     *
     * @param modEventBus mod-scoped event bus
     */
    public static void register(IEventBus modEventBus) {
        Objects.requireNonNull(modEventBus, "modEventBus").addListener(NeoForgeDataGen::onGatherData);
    }

    private static void onGatherData(GatherDataEvent event) {
        Objects.requireNonNull(event, "event");
        // No generated assets yet.
    }
}
