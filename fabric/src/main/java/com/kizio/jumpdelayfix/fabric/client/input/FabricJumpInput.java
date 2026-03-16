package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.common.api.JumpInput;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

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
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null
                && client.options != null
                && client.options.jumpKey != null
                && client.options.jumpKey.isPressed();
    }

    @Override
    public boolean isPlayerOnGround() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null
                && client.player != null
                && client.player.isOnGround();
    }

    @Override
    public void jump() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.jump();
        }
    }

    @Override
    public double getPlayerY() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return Double.NaN;
        }
        return client.player.getY();
    }

    @Override
    public int requiredGroundedTicksBeforeJump() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        if (client.isIntegratedServerRunning()) {
            return LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        return mapLatencyToRequiredTicks(getEstimatedLatencyMs(client));
    }

    @Override
    public int getLatencyMs() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.isIntegratedServerRunning()) {
            return 0;
        }

        return getEstimatedLatencyMs(client);
    }

    @Override
    public String getServerIdentifier() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return "global";
        }

        if (client.isIntegratedServerRunning()) {
            return "singleplayer";
        }

        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address != null) {
            return client.getCurrentServerEntry().address.toLowerCase(Locale.ROOT);
        }

        return "multiplayer-unknown";
    }

    private int getEstimatedLatencyMs(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null) {
            return UNKNOWN_LATENCY_MS;
        }

        var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        if (entry == null) {
            return UNKNOWN_LATENCY_MS;
        }

        return Math.max(0, entry.getLatency());
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
