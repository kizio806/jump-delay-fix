package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.fabric.client.gui.FabricSettingsScreen;
import com.kizio.jumpdelayfix.fabric.client.input.FabricKeyMappings;
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
            JumpDelayFix.onClientTick();
            if (FabricKeyMappings.consumeTogglePress()) {
                JumpDelayFix.toggleEnabled();
            }
            if (FabricKeyMappings.consumeProfileCyclePress()) {
                FabricStatusMessages.sendProfileStatus(JumpDelayFix.cycleProfile());
            }
            if (FabricKeyMappings.consumeConfigScreenPress()) {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                if (minecraftClient != null) {
                    minecraftClient.setScreen(new FabricSettingsScreen(minecraftClient.currentScreen));
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> JumpDelayFix.flushPendingConfiguration());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> JumpDelayFix.flushPendingConfiguration());
    }
}
