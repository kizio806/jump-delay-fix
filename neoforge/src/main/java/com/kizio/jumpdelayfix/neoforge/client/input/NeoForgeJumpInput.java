package com.kizio.jumpdelayfix.neoforge.client.input;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.input.LatencyCompensation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeJumpInput implements JumpInput {

    private int lastPlayerId = -1;
    private String cachedServerIdentifier = "multiplayer-unknown";
    private ServerData lastServerData;

    @Override
    public boolean isJumpPressed() {
        Minecraft client = Minecraft.getInstance();
        return client.options != null
                && client.options.keyJump != null
                && client.options.keyJump.isDown();
    }

    @Override
    public boolean isPlayerValid() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null || !player.isAlive() || player.isSleeping()
                || player.isPassenger() || player.isFallFlying()
                || player.getAbilities().flying || player.isInWater() || player.isSwimming()
                || player.isVisuallyCrawling()) {
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
    public boolean isPlayerOnGround() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        return player != null && player.onGround();
    }

    @Override
    public void jump() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player != null) {
            player.jumpFromGround();
        }
    }

    @Override
    public double getPlayerY() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return Double.NaN;
        }
        return player.getY();
    }

    @Override
    public int requiredGroundedTicksBeforeJump() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) {
            return LatencyCompensation.LOCAL_REQUIRED_GROUNDED_TICKS;
        }

        return LatencyCompensation.mapLatencyToRequiredTicks(getEstimatedLatencyMs(client));
    }

    @Override
    public int getLatencyMs() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) {
            return 0;
        }

        return getEstimatedLatencyMs(client);
    }

    @Override
    public String getServerIdentifier() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) {
            return "singleplayer";
        }

        ServerData serverData = client.getCurrentServer();
        if (serverData == null) {
            lastServerData = null;
            cachedServerIdentifier = "multiplayer-unknown";
            return cachedServerIdentifier;
        }

        if (serverData != lastServerData) {
            lastServerData = serverData;
            if (serverData.ip != null && !serverData.ip.isBlank()) {
                cachedServerIdentifier = serverData.ip.trim().toLowerCase(Locale.ROOT);
            } else {
                cachedServerIdentifier = "multiplayer-unknown";
            }
        }

        return cachedServerIdentifier;
    }

    private int getEstimatedLatencyMs(Minecraft client) {
        LocalPlayer player = client.player;
        var connection = client.getConnection();
        if (player == null || connection == null) {
            return LatencyCompensation.UNKNOWN_LATENCY_MS;
        }

        var playerInfo = connection.getPlayerInfo(player.getUUID());
        if (playerInfo == null) {
            return LatencyCompensation.UNKNOWN_LATENCY_MS;
        }

        return Math.max(0, playerInfo.getLatency());
    }
}
