package com.kizio.jumpdelayfix.neoforge.registry;

import net.neoforged.bus.api.IEventBus;

import java.util.Objects;

/**
 * Entry point for all NeoForge-side registry bootstrap.
 */
public final class NeoForgeRegistries {

    private NeoForgeRegistries() {
    }

    /**
     * Registers all deferred registries against the mod event bus.
     *
     * @param modEventBus mod-scoped event bus
     */
    public static void register(IEventBus modEventBus) {
        IEventBus bus = Objects.requireNonNull(modEventBus, "modEventBus");
        NeoForgeBlocks.register(bus);
        NeoForgeItems.register(bus);
    }
}
