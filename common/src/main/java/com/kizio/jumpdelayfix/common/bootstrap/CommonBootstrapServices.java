package com.kizio.jumpdelayfix.common.bootstrap;

import com.kizio.jumpdelayfix.common.api.IBlockRegistry;
import com.kizio.jumpdelayfix.common.api.IBlockRegistryFactory;
import com.kizio.jumpdelayfix.common.api.IItemRegistry;
import com.kizio.jumpdelayfix.common.api.IItemRegistryFactory;
import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;

import java.util.Objects;

public record CommonBootstrapServices(
        IBlockRegistryFactory blockRegistryFactory,
        IItemRegistryFactory itemRegistryFactory,
        IKeyBindingProvider keyBindingProvider
) {
    public CommonBootstrapServices {
        Objects.requireNonNull(blockRegistryFactory, "blockRegistryFactory");
        Objects.requireNonNull(itemRegistryFactory, "itemRegistryFactory");
        Objects.requireNonNull(keyBindingProvider, "keyBindingProvider");
    }
    public IBlockRegistry createBlockRegistry() {
        return Objects.requireNonNull(blockRegistryFactory.create(), "blockRegistry");
    }
    public IItemRegistry createItemRegistry() {
        return Objects.requireNonNull(itemRegistryFactory.create(), "itemRegistry");
    }
    public static CommonBootstrapServices noOp() {
        return new CommonBootstrapServices(
                () -> (path, factory) -> {
                },
                () -> (path, factory) -> {
                },
                IKeyBindingProvider.NO_OP
        );
    }
}
