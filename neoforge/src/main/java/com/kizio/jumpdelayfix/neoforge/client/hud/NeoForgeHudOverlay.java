package com.kizio.jumpdelayfix.neoforge.client.hud;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeHudOverlay {

    private static final int PANEL_WIDTH = 250;
    private static final int TEXT_X = 6;

    private NeoForgeHudOverlay() {
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!JumpDelayFix.isHudEnabled()) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }

        JumpDiagnostics diagnostics = JumpDelayFix.getDiagnostics();
        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();
        int rollbackPercent = (int) Math.round(Math.min(1.0D, Math.max(0.0D, diagnostics.rollbackRate())) * 100.0D);
        int qualityColor = qualityColor(diagnostics.rollbackRate());

        int visibleLines = countVisibleLines(config);
        int panelHeight = 26 + (visibleLines * 11) + (config.hudShowQualityBar() ? 11 : 0);

        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(config.hudOffsetX(), config.hudOffsetY(), 0.0F);
        event.getGuiGraphics().pose().scale((float) config.hudScale(), (float) config.hudScale(), 1.0F);

        event.getGuiGraphics().fill(-3, -3, PANEL_WIDTH, panelHeight, 0x90000000);
        event.getGuiGraphics().fill(-3, -3, PANEL_WIDTH, -2, 0xFF56D8FF);
        event.getGuiGraphics().drawString(client.font, Component.literal("JumpDelayFix"), TEXT_X, 5, 0x80FF9D, true);

        int y = 16;
        if (config.hudShowProfileAndPing()) {
            event.getGuiGraphics().drawString(client.font,
                    Component.literal("Profile: " + diagnostics.profile().name() + "  Ping: " + diagnostics.latencyMs() + "ms"),
                    TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowRollbackAndPenalty()) {
            event.getGuiGraphics().drawString(client.font,
                    Component.literal("Rollback: " + rollbackPercent + "%  Penalty: " + diagnostics.adaptivePenaltyTicks() + "  ReqTicks: " + diagnostics.requiredGroundedTicks()),
                    TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowModeAndQuality()) {
            event.getGuiGraphics().drawString(client.font,
                    Component.literal("Mode: " + (diagnostics.shadowMode() ? "Shadow" : "Active")
                            + "  Auto: " + diagnostics.autoProfileSwitch()
                            + "  Quality: " + qualityLabel(diagnostics.rollbackRate())),
                    TEXT_X,
                    y,
                    diagnostics.shadowMode() ? 0xFFD27F : qualityColor,
                    false
            );
            y += 11;
        }
        if (config.hudShowServer()) {
            event.getGuiGraphics().drawString(client.font,
                    Component.literal("Server: " + trimServerId(diagnostics.serverId())),
                    TEXT_X,
                    y,
                    0x9ED0FF,
                    false
            );
            y += 11;
        }

        if (config.hudShowQualityBar()) {
            int barX = TEXT_X;
            int barY = y + 2;
            int barWidth = PANEL_WIDTH - 16;
            int barHeight = 6;
            int filledWidth = (int) Math.round((rollbackPercent / 100.0D) * barWidth);

            event.getGuiGraphics().fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
            if (filledWidth > 0) {
                event.getGuiGraphics().fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | qualityColor);
            }
        }

        event.getGuiGraphics().pose().popPose();
    }

    private static int countVisibleLines(JumpRuntimeConfig config) {
        int count = 0;
        if (config.hudShowProfileAndPing()) {
            count++;
        }
        if (config.hudShowRollbackAndPenalty()) {
            count++;
        }
        if (config.hudShowModeAndQuality()) {
            count++;
        }
        if (config.hudShowServer()) {
            count++;
        }
        return count;
    }

    private static int qualityColor(double rollbackRate) {
        if (rollbackRate >= 0.35D) {
            return 0xFF6A6A;
        }
        if (rollbackRate >= 0.15D) {
            return 0xFFCA6D;
        }
        return 0x7CFFB4;
    }

    private static String qualityLabel(double rollbackRate) {
        if (rollbackRate >= 0.35D) {
            return "High Risk";
        }
        if (rollbackRate >= 0.15D) {
            return "Moderate";
        }
        return "Stable";
    }

    private static String trimServerId(String serverId) {
        if (serverId == null || serverId.isBlank()) {
            return "n/a";
        }
        if (serverId.length() <= 28) {
            return serverId;
        }
        return serverId.substring(0, 28) + "...";
    }
}
