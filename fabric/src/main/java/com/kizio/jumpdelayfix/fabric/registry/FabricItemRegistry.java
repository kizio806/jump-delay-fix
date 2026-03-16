package com.kizio.jumpdelayfix.fabric.registry;

import com.kizio.jumpdelayfix.common.ModConstants;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class FabricItemRegistry {

    private FabricItemRegistry() {
    }

    public static void register() {
        // No item content yet. Keep registration helper for future additions.
    }

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(ModConstants.MOD_ID, path), item);
    }
}
