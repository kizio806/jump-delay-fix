package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.fabric.client.FabricClientBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class JumpDelayFixFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricClientBootstrap.initialize();
    }
}
