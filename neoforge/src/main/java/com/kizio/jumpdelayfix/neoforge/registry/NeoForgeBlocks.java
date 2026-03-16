package com.kizio.jumpdelayfix.neoforge.registry;

import com.kizio.jumpdelayfix.common.ModConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

/**
 * Owns NeoForge block deferred registrations for this mod.
 */
public final class NeoForgeBlocks {

    private static final DeferredRegister.Blocks BLOCK_REGISTRY = DeferredRegister.createBlocks(ModConstants.MOD_ID);

    private NeoForgeBlocks() {
    }

    /**
     * Registers all block deferred registers to the mod event bus.
     *
     * @param modEventBus mod-scoped event bus
     */
    public static void register(IEventBus modEventBus) {
        BLOCK_REGISTRY.register(Objects.requireNonNull(modEventBus, "modEventBus"));
    }
}
