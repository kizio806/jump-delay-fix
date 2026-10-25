package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.Constants;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

public final class FabricHeadlessGameTest {

    @GameTest
    public void shouldBootHeadlessFabricGameTestServer(TestContext context) {
        context.assertTrue(
                Constants.MOD_ID.equals("jumpdelayfix"),
                Text.literal("Expected common constants to be available during Fabric GameTest bootstrap")
        );
        context.complete();
    }
}
