package com.kizio.jumpdelayfix.neoforge.registry;

import com.kizio.jumpdelayfix.common.ModConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

/**
 * Owns NeoForge item deferred registrations for this mod.
 */
public final class NeoForgeItems {

    private static final DeferredRegister.Items ITEM_REGISTRY = DeferredRegister.createItems(ModConstants.MOD_ID);

    private NeoForgeItems() {
    }

    /**
     * Registers all item deferred registers to the mod event bus.
     *
     * @param modEventBus mod-scoped event bus
     */
    public static void register(IEventBus modEventBus) {
        ITEM_REGISTRY.register(Objects.requireNonNull(modEventBus, "modEventBus"));
    }
}
