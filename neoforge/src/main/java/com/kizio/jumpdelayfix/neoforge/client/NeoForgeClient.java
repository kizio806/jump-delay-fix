package com.kizio.jumpdelayfix.neoforge.client;

import com.kizio.jumpdelayfix.JumpDelayFix;
import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.neoforge.client.gui.NeoForgeSettingsScreen;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeJumpInput;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeKeyMappings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);
    private static boolean initialized;

    private NeoForgeClient() {
    }

    public static synchronized void init(IEventBus modEventBus) {
        if (initialized) {
            return;
        }

        LOGGER.info("{} NeoForge client starting", Constants.MOD_NAME);
        modEventBus.addListener(NeoForgeKeyMappings::onRegisterKeyMappings);
        modEventBus.addListener(NeoForgeClient::onClientSetup);
        NeoForge.EVENT_BUS.addListener(NeoForgeClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(NeoForgeClient::onClientLogout);
        initialized = true;
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            Path configDir = client != null && client.gameDirectory != null
                    ? client.gameDirectory.toPath().resolve("config")
                    : Path.of("config");

            JumpDelayFix.init(new NeoForgeJumpInput(), NeoForgeStatusMessages::sendToggleStatus, configDir);
            LOGGER.info("{} NeoForge config directory: {}", Constants.MOD_NAME, configDir.toAbsolutePath());
        });
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        NeoForgeKeyMappings keyBindings = NeoForgeKeyMappings.getInstance();
        JumpDelayFix.onClientTick();
        if (keyBindings.consumeTogglePress()) {
            JumpDelayFix.toggleEnabled();
        }
        if (keyBindings.consumeProfileCyclePress()) {
            NeoForgeStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
        }
        if (keyBindings.consumeConfigScreenPress()) {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.setScreenAndShow(new NeoForgeSettingsScreen(client.gui.screen()));
            }
        }
    }

    private static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        JumpDelayFix.onClientDisconnect();
    }
}
