package com.kizio.jumpdelayfix.neoforge.registry;

import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrapServices;

public final class NeoForgeRegistries {

    private NeoForgeRegistries() {

    }

    public static CommonBootstrapServices createBootstrapServices() {
        return new CommonBootstrapServices(
                NeoForgeBlocks::getInstance,
                NeoForgeItems::getInstance,
                IKeyBindingProvider.NO_OP
        );
    }
}
