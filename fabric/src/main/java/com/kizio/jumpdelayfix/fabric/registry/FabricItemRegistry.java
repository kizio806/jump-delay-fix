package com.kizio.jumpdelayfix.fabric.registry;

import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import com.kizio.jumpdelayfix.common.api.IItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public final class FabricItemRegistry implements IItemRegistry {

    private static final FabricItemRegistry INSTANCE = new FabricItemRegistry();

    private FabricItemRegistry() {

    }

    public static FabricItemRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(String path, Supplier<?> factory) {
        Item item = (Item) Objects.requireNonNull(factory.get(), "factory");
        Registry.register(Registries.ITEM, Identifier.of(JumpDelayFixConstants.MOD_ID, path), item);
    }
}
