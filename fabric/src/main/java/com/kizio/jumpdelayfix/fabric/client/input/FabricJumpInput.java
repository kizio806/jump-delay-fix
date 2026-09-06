package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.input.JumpInput;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public final class FabricJumpInput implements JumpInput {

    private static final int LOCAL_REQUIRED_GROUNDED_TICKS = 1;
    private static final int REMOTE_TICKS_LOW_LATENCY = 2;
    private static final int REMOTE_TICKS_MEDIUM_LATENCY = 3;
    private static final int REMOTE_TICKS_HIGH_LATENCY = 4;
    private static final int REMOTE_TICKS_VERY_HIGH_LATENCY = 5;

    private static final int LOW_LATENCY_MAX_MS = 80;
    private static final int MEDIUM_LATENCY_MAX_MS = 150;
    private static final int HIGH_LATENCY_MAX_MS = 250;
    private static final int UNKNOWN_LATENCY_MS = -1;
    private static final int UNKNOWN_LATENCY_FALLBACK_TICKS = REMOTE_TICKS_MEDIUM_LATENCY;

    @Override
    public boolean isJumpPressed() {
        Minecraft client = Minecraft.getInstance();
        return client != null
                && client.options != null
                && client.options.keyJump != null
                && client.options.keyJump.isDown();
    }

    @Override
    public boolean isPlayerOnGround() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client != null ? client.player : null;
        return player != null && player.onGround();
    }

    @Override
    public void jump() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client != null ? client.player : null;
        if (player != null) {
            player.jumpFromGround();
        }
    }
    @Override
    public double getPlayerY() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client != null ? client.player : null;
        if (player == null) {
            return Double.NaN;
        }
        return player.getY();
    }

    @Override
    public int requiredGroundedTicksBeforeJump() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        if (client.hasSingleplayerServer()) {
            return LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        return mapLatencyToRequiredTicks(getEstimatedLatencyMs(client));
    }

    @Override
    public int getLatencyMs() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.hasSingleplayerServer()) {
            return 0;
        }

        return getEstimatedLatencyMs(client);
    }

    @Override
    public String getServerIdentifier() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return "global";
        }

        if (client.hasSingleplayerServer()) {
            return "singleplayer";
        }

        var serverData = client.getCurrentServer();
        if (serverData != null && serverData.ip != null && !serverData.ip.isBlank()) {
            return serverData.ip.toLowerCase(Locale.ROOT);
        }

        return "multiplayer-unknown";
    }
    private int getEstimatedLatencyMs(Minecraft client) {
        LocalPlayer player = client.player;
        var connection = client.getConnection();
        if (player == null || connection == null) {
            return UNKNOWN_LATENCY_MS;
        }

        var playerInfo = connection.getPlayerInfo(player.getUUID());
        if (playerInfo == null) {
            return UNKNOWN_LATENCY_MS;
        }

        return Math.max(0, playerInfo.getLatency());
    }

    private int mapLatencyToRequiredTicks(int latencyMs) {
        if (latencyMs == UNKNOWN_LATENCY_MS) {
            return UNKNOWN_LATENCY_FALLBACK_TICKS;
        }

        if (latencyMs <= LOW_LATENCY_MAX_MS) {
            return REMOTE_TICKS_LOW_LATENCY;
        }
        if (latencyMs <= MEDIUM_LATENCY_MAX_MS) {
            return REMOTE_TICKS_MEDIUM_LATENCY;
        }
        if (latencyMs <= HIGH_LATENCY_MAX_MS) {
            return REMOTE_TICKS_HIGH_LATENCY;
        }

        return REMOTE_TICKS_VERY_HIGH_LATENCY;
    }
}
