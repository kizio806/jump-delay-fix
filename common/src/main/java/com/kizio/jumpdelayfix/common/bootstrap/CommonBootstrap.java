package com.kizio.jumpdelayfix.common.bootstrap;

import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;
import com.kizio.jumpdelayfix.common.registry.CommonBlockRegistry;
import com.kizio.jumpdelayfix.common.registry.CommonItemRegistry;

import java.util.Objects;

public final class CommonBootstrap {

    private static boolean bootstrapped;
    private static CommonBootstrapServices services = CommonBootstrapServices.noOp();

    private CommonBootstrap() {
    }

    public static synchronized void bootstrap(CommonBootstrapServices bootstrapServices) {
        if (bootstrapped) {
            return;
        }

        services = Objects.requireNonNull(bootstrapServices, "bootstrapServices");
        CommonBlockRegistry.registerAll(services.createBlockRegistry());
        CommonItemRegistry.registerAll(services.createItemRegistry());
        bootstrapped = true;
    }

    public static synchronized IKeyBindingProvider getKeyBindingProvider() {
        return services.keyBindingProvider();
    }

    public static synchronized void installClientServices(IKeyBindingProvider keyBindingProvider) {
        services = new CommonBootstrapServices(
                services.blockRegistryFactory(),
                services.itemRegistryFactory(),
                Objects.requireNonNull(keyBindingProvider, "keyBindingProvider")
        );
    }

    static synchronized void resetForTests() {
        bootstrapped = false;
        services = CommonBootstrapServices.noOp();
    }
}
