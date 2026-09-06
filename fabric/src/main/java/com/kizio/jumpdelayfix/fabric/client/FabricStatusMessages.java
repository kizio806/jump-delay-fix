package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.model.JumpProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

@Environment(EnvType.CLIENT)
public final class FabricStatusMessages {

    private FabricStatusMessages() {
    }

    public static void sendToggleStatus(boolean enabled) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableComponent stateText = Component.translatable(enabled
                ? "message.jumpdelayfix.enabled"
                : "message.jumpdelayfix.disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED);

        client.player.sendOverlayMessage(Component.translatable("message.jumpdelayfix.status", stateText));
    }

    public static void sendProfileStatus(JumpProfile profile) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableComponent profileText = Component.translatable(profile.translationKey()).withStyle(ChatFormatting.AQUA);
        client.player.sendOverlayMessage(Component.translatable("message.jumpdelayfix.profile_status", profileText));
    }
}
