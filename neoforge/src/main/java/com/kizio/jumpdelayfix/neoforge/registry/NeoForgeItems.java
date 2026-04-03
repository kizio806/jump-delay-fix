package com.kizio.jumpdelayfix.neoforge.registry;

import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import com.kizio.jumpdelayfix.common.api.IItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.function.Supplier;
public final class NeoForgeItems implements IItemRegistry {

    private static final NeoForgeItems INSTANCE = new NeoForgeItems();

    private NeoForgeItems() {

    }

    public static NeoForgeItems getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(String path, Supplier<?> factory) {
        Item item = (Item) Objects.requireNonNull(factory.get(), "factory");
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(JumpDelayFixConstants.MOD_ID, path), item);
    }
}
