package com.kizio.jumpdelayfix.fabric.registry;

import com.kizio.jumpdelayfix.common.ModConstants;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class FabricBlockRegistry {

    private FabricBlockRegistry() {
    }

    public static void register() {
        // No block content yet. Keep registration helper for future additions.
    }

    public static <T extends Block> T register(String path, T block) {
        return Registry.register(Registries.BLOCK, Identifier.of(ModConstants.MOD_ID, path), block);
    }
}
