package com.kizio.jumpdelayfix.common.registry;

import com.kizio.jumpdelayfix.common.api.IItemRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class CommonItemRegistry {

    private static final Map<String, Supplier<?>> ITEMS = Map.of();

    private CommonItemRegistry() {

    }
    public static void registerAll(IItemRegistry registry) {
        IItemRegistry resolvedRegistry = Objects.requireNonNull(registry, "registry");
        ITEMS.forEach(resolvedRegistry::register);
    }
}
