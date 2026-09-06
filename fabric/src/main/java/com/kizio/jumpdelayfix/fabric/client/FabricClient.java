package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.JumpDelayFix;
import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.fabric.client.input.FabricJumpInput;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
import com.kizio.jumpdelayfix.fabric.client.gui.FabricSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public final class FabricClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);
    private static boolean initialized;
    private FabricClient() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        LOGGER.info("{} Fabric client starting", Constants.MOD_NAME);
        FabricKeyMappings keyBindings = FabricKeyMappings.getInstance();
        keyBindings.register();

        Minecraft client = Minecraft.getInstance();
        Path configDir = client != null && client.gameDirectory != null
                ? client.gameDirectory.toPath().resolve("config")
                : Path.of("config");

        JumpDelayFix.init(new FabricJumpInput(), FabricStatusMessages::sendToggleStatus, configDir);
        LOGGER.info("{} Fabric config directory: {}", Constants.MOD_NAME, configDir.toAbsolutePath());

        registerEvents(keyBindings);
        initialized = true;
    }

    private static void registerEvents(FabricKeyMappings keyBindings) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            JumpDelayFix.onClientTick();
            if (keyBindings.consumeTogglePress()) {
                JumpDelayFix.toggleEnabled();
            }
            if (keyBindings.consumeProfileCyclePress()) {
                FabricStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
            }
            if (keyBindings.consumeConfigScreenPress()) {
                if (client != null) {
                    client.setScreenAndShow(new FabricSettingsScreen(client.gui.screen()));
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> JumpDelayFix.onClientDisconnect());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> JumpDelayFix.flushPendingConfiguration());
    }
}
