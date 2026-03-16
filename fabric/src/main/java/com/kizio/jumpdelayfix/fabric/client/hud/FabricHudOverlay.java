package com.kizio.jumpdelayfix.fabric.client.hud;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.ModConstants;
import com.kizio.jumpdelayfix.common.client.hud.HudLine;
import com.kizio.jumpdelayfix.common.client.hud.HudPresentationModel;
import com.kizio.jumpdelayfix.common.client.hud.HudPresentationService;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import com.kizio.jumpdelayfix.fabric.client.render.FabricMatrixStackCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class FabricHudOverlay {

    private static final Identifier HUD_ELEMENT_ID = Identifier.of(ModConstants.MOD_ID, "overlay");
    private static boolean registered;

    private FabricHudOverlay() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, HUD_ELEMENT_ID, (drawContext, tickCounter) -> renderOverlay(drawContext));
        registered = true;
    }

    private static void renderOverlay(net.minecraft.client.gui.DrawContext drawContext) {
        if (!JumpDelayFix.isHudEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null || client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        JumpDiagnostics diagnostics = JumpDelayFix.getDiagnostics();
        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();
        HudPresentationModel model = HudPresentationService.build(diagnostics, config);

        try (FabricMatrixStackCompat.ScopedMatrixTransform transform =
                     FabricMatrixStackCompat.pushTranslateScale(drawContext, config.hudOffsetX(), config.hudOffsetY(), config.hudScale())) {
            if (!transform.isActive()) {
                return;
            }
            drawContext.fill(-3, -3, HudPresentationService.PANEL_WIDTH, model.panelHeight(), 0x90000000);
            drawContext.fill(-3, -3, HudPresentationService.PANEL_WIDTH, -2, 0xFF56D8FF);
            drawContext.drawText(client.textRenderer, Text.literal(HudPresentationService.HUD_TITLE), HudPresentationService.TEXT_X, 5, 0x80FF9D, true);

            int y = 16;
            for (HudLine line : model.lines()) {
                drawContext.drawText(client.textRenderer,
                        Text.literal(line.text()),
                        HudPresentationService.TEXT_X,
                        y,
                        line.color(),
                        false
                );
                y += HudPresentationService.LINE_HEIGHT;
            }

            if (model.showQualityBar()) {
                int barX = HudPresentationService.TEXT_X;
                int barY = y + 2;
                int barWidth = HudPresentationService.PANEL_WIDTH - 16;
                int barHeight = 6;
                int filledWidth = model.qualityBarFilledWidth(barWidth);

                drawContext.fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
                if (filledWidth > 0) {
                    drawContext.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | model.qualityColor());
                }
            }
        }
    }
}
