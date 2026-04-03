package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;
import com.kizio.jumpdelayfix.common.bootstrap.CommonBootstrap;
import com.kizio.jumpdelayfix.fabric.client.gui.FabricSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public final class FabricEvents {

    private FabricEvents() {

    }

    public static void registerClientEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            IKeyBindingProvider keyBindings = CommonBootstrap.getKeyBindingProvider();
            JumpDelayFix.onClientTick();
            if (keyBindings.consumeTogglePress()) {
                JumpDelayFix.toggleEnabled();
            }
            if (keyBindings.consumeProfileCyclePress()) {
                FabricStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
            }
            if (keyBindings.consumeConfigScreenPress()) {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                if (minecraftClient != null) {
                    minecraftClient.setScreen(new FabricSettingsScreen(minecraftClient.currentScreen));
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> JumpDelayFix.onClientDisconnect());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> JumpDelayFix.flushPendingConfiguration());
    }
}
