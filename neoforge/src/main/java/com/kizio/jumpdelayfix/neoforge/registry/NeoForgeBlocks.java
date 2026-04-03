package com.kizio.jumpdelayfix.neoforge.registry;

import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import com.kizio.jumpdelayfix.common.api.IBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Supplier;
public final class NeoForgeBlocks implements IBlockRegistry {

    private static final NeoForgeBlocks INSTANCE = new NeoForgeBlocks();

    private NeoForgeBlocks() {

    }

    public static NeoForgeBlocks getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(String path, Supplier<?> factory) {
        Block block = (Block) Objects.requireNonNull(factory.get(), "factory");
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(JumpDelayFixConstants.MOD_ID, path), block);
    }
}
