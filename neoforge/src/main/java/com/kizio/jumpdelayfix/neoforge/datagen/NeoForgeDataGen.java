package com.kizio.jumpdelayfix.neoforge.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class NeoForgeDataGen {

    private NeoForgeDataGen() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NeoForgeDataGen::onGatherData);
    }

    private static void onGatherData(GatherDataEvent event) {
        // No generated assets yet.
    }
}
