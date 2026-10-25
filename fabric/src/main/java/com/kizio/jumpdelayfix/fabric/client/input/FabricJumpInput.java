package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.input.LatencyCompensation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public final class FabricJumpInput implements JumpInput {

    private int lastPlayerId = -1;

    @Override
    public boolean isJumpPressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.options != null
                && client.options.jumpKey != null
                && client.options.jumpKey.isPressed();
    }

    @Override
    public boolean isPlayerOnGround() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            lastPlayerId = -1;
            return false;
        }

        int currentId = client.player.getId();
        if (lastPlayerId != -1 && currentId != lastPlayerId) {
            lastPlayerId = currentId;
            return false;
        }
        lastPlayerId = currentId;

        return client.player.isAlive()
                && !client.player.isSleeping()
                && client.player.isOnGround();
    }

    @Override
    public void jump() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isAlive() && !client.player.isSleeping()) {
            client.player.jump();
        }
    }

    @Override
    public double getPlayerY() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return Double.NaN;
        }
        return client.player.getY();
    }

    @Override
    public int requiredGroundedTicksBeforeJump() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isIntegratedServerRunning()) {
            return LatencyCompensation.LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        return LatencyCompensation.mapLatencyToRequiredTicks(getEstimatedLatencyMs(client));
    }

    @Override
    public int getLatencyMs() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isIntegratedServerRunning()) {
            return 0;
        }

        return getEstimatedLatencyMs(client);
    }

    @Override
    public String getServerIdentifier() {
        MinecraftClient client = MinecraftClient.getInstance();
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
            return LatencyCompensation.UNKNOWN_LATENCY_MS;
        }

        var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        if (entry == null) {
            return LatencyCompensation.UNKNOWN_LATENCY_MS;
        }

        return Math.max(0, entry.getLatency());
    }
}
