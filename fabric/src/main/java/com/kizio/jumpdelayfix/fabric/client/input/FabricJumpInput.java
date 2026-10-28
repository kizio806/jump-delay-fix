package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.input.LatencyCompensation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public final class FabricJumpInput implements JumpInput {

    private int lastPlayerId = -1;
    private ServerInfo lastServerInfo;
    private String cachedServerIdentifier = "multiplayer-unknown";

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
        ClientPlayerEntity player = client.player;
        return player != null && player.isOnGround();
    }

    @Override
    public void jump() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player != null) {
            player.jump();
        }
    }

    @Override
    public double getPlayerY() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        return player != null ? player.getY() : Double.NaN;
    }

    @Override
    public boolean isPlayerValid() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isAlive() || player.isSleeping()
                || player.hasVehicle() || player.isGliding()
                || player.getAbilities().flying || player.isTouchingWater() || player.isSwimming()
                || player.isCrawling()) {
            lastPlayerId = -1;
            return false;
        }

        int currentId = player.getId();
        if (lastPlayerId != -1 && lastPlayerId != currentId) {
            lastPlayerId = currentId;
            return false;
        }
        lastPlayerId = currentId;
        return true;
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

        ServerInfo serverEntry = client.getCurrentServerEntry();
        if (serverEntry == null) {
            lastServerInfo = null;
            cachedServerIdentifier = "multiplayer-unknown";
            return cachedServerIdentifier;
        }

        if (serverEntry != lastServerInfo) {
            lastServerInfo = serverEntry;
            if (serverEntry.address != null && !serverEntry.address.isBlank()) {
                cachedServerIdentifier = serverEntry.address.trim().toLowerCase(Locale.ROOT);
            } else {
                cachedServerIdentifier = "multiplayer-unknown";
            }
        }

        return cachedServerIdentifier;
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
