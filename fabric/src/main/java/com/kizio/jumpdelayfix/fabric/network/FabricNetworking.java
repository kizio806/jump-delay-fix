package com.kizio.jumpdelayfix.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class FabricNetworking {

    private FabricNetworking() {
    }

    public static void registerClient() {
        // No custom packets are needed for this client-only mod.
    }
}
