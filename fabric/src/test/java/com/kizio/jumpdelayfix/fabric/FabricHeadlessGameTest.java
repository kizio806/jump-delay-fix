package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;

public final class FabricHeadlessGameTest {

    @GameTest
    public void shouldBootHeadlessFabricGameTestServer(TestContext context) {
        context.assertTrue(
                JumpDelayFixConstants.MOD_ID.equals("jumpdelayfix"),
                "Expected common constants to be available during Fabric GameTest bootstrap"
        );
        context.complete();
    }
}
