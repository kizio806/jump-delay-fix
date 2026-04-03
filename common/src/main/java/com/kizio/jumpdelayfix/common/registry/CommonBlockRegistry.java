package com.kizio.jumpdelayfix.common.registry;

import com.kizio.jumpdelayfix.common.api.IBlockRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class CommonBlockRegistry {

    private static final Map<String, Supplier<?>> BLOCKS = Map.of();

    private CommonBlockRegistry() {

    }
    public static void registerAll(IBlockRegistry registry) {
        IBlockRegistry resolvedRegistry = Objects.requireNonNull(registry, "registry");
        BLOCKS.forEach(resolvedRegistry::register);
    }
}
