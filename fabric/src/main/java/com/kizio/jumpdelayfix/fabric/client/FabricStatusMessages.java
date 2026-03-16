package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.common.model.JumpProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public final class FabricStatusMessages {

    private FabricStatusMessages() {
    }

    public static void sendToggleStatus(boolean enabled) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableText stateText = Text.translatable(enabled
                ? "message.jumpdelayfix.enabled"
                : "message.jumpdelayfix.disabled")
                .formatted(enabled ? Formatting.GREEN : Formatting.RED);

        client.player.sendMessage(Text.translatable("message.jumpdelayfix.status", stateText), true);
    }

    public static void sendProfileStatus(JumpProfile profile) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        MutableText profileText = Text.translatable(profile.translationKey()).formatted(Formatting.AQUA);
        client.player.sendMessage(Text.translatable("message.jumpdelayfix.profile_status", profileText), true);
    }

    public static void sendSimpleInfo(String translationKey) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        client.player.sendMessage(Text.translatable(translationKey).formatted(Formatting.YELLOW), true);
    }
}
