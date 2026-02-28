package com.kizio.jumpdelayfix.neoforge.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.neoforge.client.gui.NeoForgeSettingsScreen;
import com.kizio.jumpdelayfix.neoforge.client.hud.NeoForgeHudOverlay;
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

import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeClientBootstrap {

    private static boolean initialized;

    private NeoForgeClientBootstrap() {
    }

    public static synchronized void init(IEventBus modEventBus) {
        if (initialized) {
            return;
        }

        modEventBus.addListener(NeoForgeKeyMappings::onRegisterKeyMappings);
        modEventBus.addListener(NeoForgeClientBootstrap::onClientSetup);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientBootstrap::onClientTick);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientBootstrap::onClientLogout);
        NeoForge.EVENT_BUS.addListener(NeoForgeHudOverlay::onRenderGui);
        initialized = true;
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            Path configDir = client != null && client.gameDirectory != null
                    ? client.gameDirectory.toPath().resolve("config")
                    : Path.of("config");

            JumpDelayFix.init(new NeoForgeJumpInput(), NeoForgeStatusMessages::sendToggleStatus, configDir);
        });
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        JumpDelayFix.onClientTick();
        if (NeoForgeKeyMappings.consumeTogglePress()) {
            JumpDelayFix.toggleEnabled();
        }
        if (NeoForgeKeyMappings.consumeProfileCyclePress()) {
            NeoForgeStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
        }
        if (NeoForgeKeyMappings.consumeConfigScreenPress()) {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.setScreen(new NeoForgeSettingsScreen(client.screen));
            }
        }
    }

    private static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        JumpDelayFix.flushPendingConfiguration();
    }
}
