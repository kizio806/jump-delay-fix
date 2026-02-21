package com.kizio.jumpdelayfix.neoforge.registry;

import net.neoforged.bus.api.IEventBus;

public final class NeoForgeRegistries {

    private NeoForgeRegistries() {
    }

    public static void register(IEventBus modEventBus) {
        NeoForgeBlocks.register(modEventBus);
        NeoForgeItems.register(modEventBus);
    }
}
