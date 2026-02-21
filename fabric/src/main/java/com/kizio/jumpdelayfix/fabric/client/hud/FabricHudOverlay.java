package com.kizio.jumpdelayfix.fabric.client.hud;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class FabricHudOverlay {

    private static final int PANEL_WIDTH = 250;
    private static final int TEXT_X = 6;

    private FabricHudOverlay() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!JumpDelayFix.isHudEnabled()) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.options == null || client.getDebugHud().shouldShowDebugHud()) {
                return;
            }

            JumpDiagnostics diagnostics = JumpDelayFix.getDiagnostics();
            JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();
            int rollbackPercent = (int) Math.round(Math.min(1.0D, Math.max(0.0D, diagnostics.rollbackRate())) * 100.0D);
            int qualityColor = qualityColor(diagnostics.rollbackRate());

            int visibleLines = countVisibleLines(config);
            int panelHeight = 26 + (visibleLines * 11) + (config.hudShowQualityBar() ? 11 : 0);

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(config.hudOffsetX(), config.hudOffsetY(), 0.0F);
            drawContext.getMatrices().scale((float) config.hudScale(), (float) config.hudScale(), 1.0F);

            drawContext.fill(-3, -3, PANEL_WIDTH, panelHeight, 0x90000000);
            drawContext.fill(-3, -3, PANEL_WIDTH, -2, 0xFF56D8FF);
            drawContext.drawText(client.textRenderer, Text.literal("JumpDelayFix"), TEXT_X, 5, 0x80FF9D, true);

            int y = 16;
            if (config.hudShowProfileAndPing()) {
                drawContext.drawText(client.textRenderer,
                        Text.literal("Profile: " + diagnostics.profile().name() + "  Ping: " + diagnostics.latencyMs() + "ms"),
                        TEXT_X,
                        y,
                        0xFFFFFF,
                        false
                );
                y += 11;
            }
            if (config.hudShowRollbackAndPenalty()) {
                drawContext.drawText(client.textRenderer,
                        Text.literal("Rollback: " + rollbackPercent + "%  Penalty: " + diagnostics.adaptivePenaltyTicks() + "  ReqTicks: " + diagnostics.requiredGroundedTicks()),
                        TEXT_X,
                        y,
                        0xFFFFFF,
                        false
                );
                y += 11;
            }
            if (config.hudShowModeAndQuality()) {
                drawContext.drawText(client.textRenderer,
                        Text.literal("Mode: " + (diagnostics.shadowMode() ? "Shadow" : "Active")
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
                drawContext.drawText(client.textRenderer,
                        Text.literal("Server: " + trimServerId(diagnostics.serverId())),
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

                drawContext.fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
                if (filledWidth > 0) {
                    drawContext.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | qualityColor);
                }
            }

            drawContext.getMatrices().pop();
        });
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
