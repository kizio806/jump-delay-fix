package com.kizio.jumpdelayfix.common.bootstrap;

import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;
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
        TrackingKeyBindingProvider keyBindingProvider = new TrackingKeyBindingProvider();

        CommonBootstrap.bootstrap(new CommonBootstrapServices(
                () -> (path, factory) -> {
                },
                () -> (path, factory) -> {
                },
                keyBindingProvider
        ));

        assertTrue(CommonBootstrap.getKeyBindingProvider() == keyBindingProvider);
    }

    private static final class TrackingKeyBindingProvider implements IKeyBindingProvider {
        @Override
        public boolean consumeTogglePress() {
            return false;
        }

        @Override
        public boolean consumeProfileCyclePress() {
            return false;
        }

        @Override
        public boolean consumeConfigScreenPress() {
            return false;
        }
    }
}
