package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrap;
import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
import com.kizio.jumpdelayfix.fabric.client.hud.FabricHudOverlay;
import com.kizio.jumpdelayfix.fabric.network.FabricNetworking;
import com.kizio.jumpdelayfix.fabric.registry.FabricBlockRegistry;
import com.kizio.jumpdelayfix.fabric.registry.FabricItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public final class FabricClientBootstrap {

    private static boolean initialized;

    private FabricClientBootstrap() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        CommonBootstrap.bootstrap();
        FabricBlockRegistry.register();
        FabricItemRegistry.register();
        FabricNetworking.registerClient();
        FabricKeyMappings.register();

        MinecraftClient client = MinecraftClient.getInstance();
        Path configDir = client != null && client.runDirectory != null
                ? client.runDirectory.toPath().resolve("config")
                : Path.of("config");

        JumpDelayFix.init(new FabricJumpInput(), FabricStatusMessages::sendToggleStatus, configDir);
        FabricEvents.registerClientEvents();
        FabricHudOverlay.register();
        initialized = true;
    }
}
