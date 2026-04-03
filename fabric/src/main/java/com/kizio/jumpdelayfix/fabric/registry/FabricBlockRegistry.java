package com.kizio.jumpdelayfix.fabric.registry;

import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import com.kizio.jumpdelayfix.common.api.IBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public final class FabricBlockRegistry implements IBlockRegistry {

    private static final FabricBlockRegistry INSTANCE = new FabricBlockRegistry();

    private FabricBlockRegistry() {

    }

    public static FabricBlockRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(String path, Supplier<?> factory) {
        Block block = (Block) Objects.requireNonNull(factory.get(), "factory");
        Registry.register(Registries.BLOCK, Identifier.of(JumpDelayFixConstants.MOD_ID, path), block);
    }
}
