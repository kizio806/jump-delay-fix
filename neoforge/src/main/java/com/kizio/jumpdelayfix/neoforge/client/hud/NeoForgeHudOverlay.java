package com.kizio.jumpdelayfix.neoforge.client.hud;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.client.hud.HudLine;
import com.kizio.jumpdelayfix.common.client.hud.HudPresentationModel;
import com.kizio.jumpdelayfix.common.client.hud.HudPresentationService;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeHudOverlay {

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
        HudPresentationModel model = HudPresentationService.build(diagnostics, config);

        event.getGuiGraphics().pose().pushPose();
        try {
            event.getGuiGraphics().pose().translate(config.hudOffsetX(), config.hudOffsetY(), 0.0F);
            event.getGuiGraphics().pose().scale((float) config.hudScale(), (float) config.hudScale(), 1.0F);

            event.getGuiGraphics().fill(-3, -3, HudPresentationService.PANEL_WIDTH, model.panelHeight(), 0x90000000);
            event.getGuiGraphics().fill(-3, -3, HudPresentationService.PANEL_WIDTH, -2, 0xFF56D8FF);
            event.getGuiGraphics().drawString(client.font, Component.literal(HudPresentationService.HUD_TITLE), HudPresentationService.TEXT_X, 5, 0x80FF9D, true);

            int y = 16;
            for (HudLine line : model.lines()) {
                event.getGuiGraphics().drawString(client.font,
                        Component.literal(line.text()),
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

                event.getGuiGraphics().fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
                if (filledWidth > 0) {
                    event.getGuiGraphics().fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | model.qualityColor());
                }
            }
        } finally {
            event.getGuiGraphics().pose().popPose();
        }
    }
}
