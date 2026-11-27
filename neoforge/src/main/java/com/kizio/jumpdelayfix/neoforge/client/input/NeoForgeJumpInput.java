package com.kizio.jumpdelayfix.neoforge.client.input;

import com.kizio.jumpdelayfix.input.JumpInput;
import com.kizio.jumpdelayfix.input.LatencyCompensation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeJumpInput implements JumpInput {

    private int lastPlayerId = -1;
    private ServerData lastServerData;
    private String cachedServerIdentifier = "multiplayer-unknown";
    private int cachedLatencyMs = LatencyCompensation.UNKNOWN_LATENCY_MS;
    private int latencyQueryCooldownTicks = 0;

    @Override
    public boolean isJumpPressed() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.screen != null) {
            return false;
        }
        LocalPlayer player = client.player;
        if (player != null && player.input != null && player.input.keyPresses != null) {
            return player.input.keyPresses.jump();
        }
        return client.options != null
                && client.options.keyJump != null
                && client.options.keyJump.isDown();
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
        return player != null ? player.getY() : Double.NaN;
    }

    @Override
    public boolean isPlayerValid() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.screen != null) {
            return false;
        }
        LocalPlayer player = client.player;
        if (player == null || !player.isAlive() || player.isSleeping()
                || player.isPassenger() || player.isFallFlying()
                || player.getAbilities().flying || player.isInWater() || player.isSwimming()
                || player.isVisuallyCrawling() || player.onClimbable() || player.hasEffect(MobEffects.LEVITATION)) {
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
        if (latencyQueryCooldownTicks > 0 && cachedLatencyMs != LatencyCompensation.UNKNOWN_LATENCY_MS) {
            latencyQueryCooldownTicks--;
            return cachedLatencyMs;
        }
        latencyQueryCooldownTicks = 20;

        LocalPlayer player = client.player;
        var connection = client.getConnection();
        if (player == null || connection == null) {
            cachedLatencyMs = LatencyCompensation.UNKNOWN_LATENCY_MS;
            return cachedLatencyMs;
        }

        var playerInfo = connection.getPlayerInfo(player.getUUID());
        if (playerInfo == null) {
            cachedLatencyMs = LatencyCompensation.UNKNOWN_LATENCY_MS;
            return cachedLatencyMs;
        }

        cachedLatencyMs = Math.max(0, playerInfo.getLatency());
        return cachedLatencyMs;
    }
}
