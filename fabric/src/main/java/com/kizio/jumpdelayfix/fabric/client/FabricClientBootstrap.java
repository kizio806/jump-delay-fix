package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.JumpDelayFixConstants;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrap;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrapServices;
import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpDelayFixConstants.MOD_ID);
    private static boolean initialized;

    private FabricClientBootstrap() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        LOGGER.info("{} Fabric client bootstrap starting", JumpDelayFixConstants.MOD_NAME);
        FabricKeyMappings keyBindings = FabricKeyMappings.getInstance();
        CommonBootstrap.bootstrap(new CommonBootstrapServices(
                FabricBlockRegistry::getInstance,
                FabricItemRegistry::getInstance,
                keyBindings
        ));
        keyBindings.register();

        MinecraftClient client = MinecraftClient.getInstance();
        Path configDir = client != null && client.runDirectory != null
                ? client.runDirectory.toPath().resolve("config")
                : Path.of("config");

        JumpDelayFix.init(new FabricJumpInput(), FabricStatusMessages::sendToggleStatus, configDir);
        LOGGER.info("{} Fabric config directory: {}", JumpDelayFixConstants.MOD_NAME, configDir.toAbsolutePath());
        FabricEvents.registerClientEvents();
        initialized = true;
    }
}
