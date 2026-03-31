package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.ModConstants;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrap;
import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
import com.kizio.jumpdelayfix.fabric.network.FabricNetworking;
import com.kizio.jumpdelayfix.fabric.registry.FabricBlockRegistry;
import com.kizio.jumpdelayfix.fabric.registry.FabricItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public final class FabricClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModConstants.MOD_NAME);
    private static boolean initialized;

    private FabricClientBootstrap() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        LOGGER.info("{} Fabric client bootstrap starting", ModConstants.MOD_NAME);
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
        LOGGER.info("{} Fabric config directory: {}", ModConstants.MOD_NAME, configDir.toAbsolutePath());
        FabricEvents.registerClientEvents();
        initialized = true;
    }
}
