package com.kizio.jumpdelayfix.neoforge.network;

import net.neoforged.bus.api.IEventBus;

public final class NeoForgeNetworking {

    private NeoForgeNetworking() {
    }

    public static void register(IEventBus modEventBus) {
        // No custom packets are needed for this client-only mod.
    }
}
