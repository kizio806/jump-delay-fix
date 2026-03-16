package com.kizio.jumpdelayfix.neoforge.client;

import com.kizio.jumpdelayfix.common.model.JumpProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeStatusMessages {

    private NeoForgeStatusMessages() {
    }

    public static void sendToggleStatus(boolean enabled) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableComponent state = Component.translatable(enabled
                ? "message.jumpdelayfix.enabled"
                : "message.jumpdelayfix.disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED);

        client.player.displayClientMessage(Component.translatable("message.jumpdelayfix.status", state), true);
    }

    public static void sendProfileStatus(JumpProfile profile) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableComponent profileComponent = Component.translatable(profile.translationKey()).withStyle(ChatFormatting.AQUA);
        client.player.displayClientMessage(Component.translatable("message.jumpdelayfix.profile_status", profileComponent), true);
    }

    public static void sendSimpleInfo(String translationKey) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        client.player.displayClientMessage(Component.translatable(translationKey).withStyle(ChatFormatting.YELLOW), true);
    }
}
