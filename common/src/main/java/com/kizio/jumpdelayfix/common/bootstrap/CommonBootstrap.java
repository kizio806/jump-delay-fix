package com.kizio.jumpdelayfix.common.bootstrap;

import com.kizio.jumpdelayfix.common.event.CommonEvents;
import com.kizio.jumpdelayfix.common.network.CommonNetworking;
import com.kizio.jumpdelayfix.common.registry.CommonBlockRegistry;
import com.kizio.jumpdelayfix.common.registry.CommonItemRegistry;

public final class CommonBootstrap {

    private static boolean bootstrapped;

    private CommonBootstrap() {
    }

    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }

        CommonBlockRegistry.register();
        CommonItemRegistry.register();
        CommonEvents.register();
        CommonNetworking.register();
        bootstrapped = true;
    }

    static synchronized void resetForTests() {
        bootstrapped = false;
        CommonBlockRegistry.resetForTests();
        CommonItemRegistry.resetForTests();
        CommonEvents.resetForTests();
        CommonNetworking.resetForTests();
    }
}
