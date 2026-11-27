package com.kizio.jumpdelayfix.neoforge.client;

import com.kizio.jumpdelayfix.JumpDelayFix;
import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.neoforge.client.gui.NeoForgeSettingsScreen;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeJumpInput;
import com.kizio.jumpdelayfix.neoforge.client.input.NeoForgeKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new NeoForgeSettingsScreen(screen))
        );
        modEventBus.addListener(NeoForgeKeyMappings::onRegisterKeyMappings);
        modEventBus.addListener(NeoForgeClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(NeoForgeClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(NeoForgeClient::onClientLogout);
        MinecraftForge.EVENT_BUS.addListener(NeoForgeClient::onClientPlayerClone);
        MinecraftForge.EVENT_BUS.addListener((GameShuttingDownEvent event) -> {
            JumpDelayFix.flushPendingConfiguration();
            JumpDelayFix.shutdown();
        });
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

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!JumpDelayFix.isInitialized()) {
            return;
        }

        NeoForgeKeyMappings keyBindings = NeoForgeKeyMappings.getInstance();
        while (keyBindings.consumeTogglePress()) {
            JumpDelayFix.toggleEnabled();
        }
        while (keyBindings.consumeProfileCyclePress()) {
            NeoForgeStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
        }
        if (keyBindings.consumeConfigScreenPress()) {
            Minecraft client = Minecraft.getInstance();
            if (client != null && client.screen == null) {
                client.setScreen(new NeoForgeSettingsScreen(client.screen));
            }
        }
        JumpDelayFix.onClientTick();
    }

    private static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        JumpDelayFix.onClientDisconnect();
    }

    private static void onClientPlayerClone(ClientPlayerNetworkEvent.Clone event) {
        JumpDelayFix.resetTransientState();
    }
}
