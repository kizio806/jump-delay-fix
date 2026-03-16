package com.kizio.jumpdelayfix.common.bootstrap;

import com.kizio.jumpdelayfix.common.event.CommonEvents;
import com.kizio.jumpdelayfix.common.network.CommonNetworking;
import com.kizio.jumpdelayfix.common.registry.CommonBlockRegistry;
import com.kizio.jumpdelayfix.common.registry.CommonItemRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonBootstrapTest {

    @AfterEach
    void tearDown() {
        CommonBootstrap.resetForTests();
    }

    @Test
    void shouldRegisterCommonPipelines() {
        CommonBootstrap.bootstrap();

        assertTrue(CommonBlockRegistry.isRegistered());
        assertTrue(CommonItemRegistry.isRegistered());
        assertTrue(CommonEvents.isRegistered());
        assertTrue(CommonNetworking.isRegistered());
    }
}
