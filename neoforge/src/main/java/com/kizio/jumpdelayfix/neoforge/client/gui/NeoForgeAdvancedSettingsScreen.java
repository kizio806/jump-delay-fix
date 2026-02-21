package com.kizio.jumpdelayfix.neoforge.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeStatusMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeAdvancedSettingsScreen extends Screen {

    private static final int MAX_PANEL_WIDTH = 430;
    private static final int MAX_PANEL_HEIGHT = 300;
    private static final int PANEL_PADDING = 12;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_SPACING = 22;
    private static final int BUTTON_GAP = 8;
    private static final double STEP = 0.01D;

    private final Screen parent;

    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;

    private int minAttemptsRowY;
    private int competitiveRateRowY;
    private int stableRateRowY;
    private int failsafeRateRowY;

    private EditBox presetField;

    public NeoForgeAdvancedSettingsScreen(Screen parent) {
        super(Component.translatable("gui.jumpdelayfix.section.adaptive"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        panelWidth = Math.min(MAX_PANEL_WIDTH, this.width - 16);
        panelHeight = Math.min(MAX_PANEL_HEIGHT, this.height - 16);
        panelLeft = (this.width - panelWidth) / 2;
        panelTop = (this.height - panelHeight) / 2;

        int left = panelLeft + PANEL_PADDING;
        int contentWidth = panelWidth - (PANEL_PADDING * 2);
        int halfWidth = (contentWidth - BUTTON_GAP) / 2;
        int rowY = panelTop + 58;

        minAttemptsRowY = rowY;
        addStepperButtons(minAttemptsRowY,
                () -> JumpDelayFix.setMinAttemptsForProfileSwitch(JumpDelayFix.getRuntimeConfig().minAttemptsForProfileSwitch() - 1),
                () -> JumpDelayFix.setMinAttemptsForProfileSwitch(JumpDelayFix.getRuntimeConfig().minAttemptsForProfileSwitch() + 1)
        );

        competitiveRateRowY = rowY + ROW_SPACING;
        addStepperButtons(competitiveRateRowY,
                () -> JumpDelayFix.setCompetitiveRollbackRateMax(JumpDelayFix.getRuntimeConfig().competitiveRollbackRateMax() - STEP),
                () -> JumpDelayFix.setCompetitiveRollbackRateMax(JumpDelayFix.getRuntimeConfig().competitiveRollbackRateMax() + STEP)
        );

        stableRateRowY = rowY + (ROW_SPACING * 2);
        addStepperButtons(stableRateRowY,
                () -> JumpDelayFix.setStableRollbackRateMin(JumpDelayFix.getRuntimeConfig().stableRollbackRateMin() - STEP),
                () -> JumpDelayFix.setStableRollbackRateMin(JumpDelayFix.getRuntimeConfig().stableRollbackRateMin() + STEP)
        );

        failsafeRateRowY = rowY + (ROW_SPACING * 3);
        addStepperButtons(failsafeRateRowY,
                () -> JumpDelayFix.setFailsafeRollbackRate(JumpDelayFix.getRuntimeConfig().failsafeRollbackRate() - STEP),
                () -> JumpDelayFix.setFailsafeRollbackRate(JumpDelayFix.getRuntimeConfig().failsafeRollbackRate() + STEP)
        );

        presetField = new EditBox(this.font, left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT, Component.translatable("gui.jumpdelayfix.preset"));
        presetField.setMaxLength(512);
        this.addRenderableWidget(presetField);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.preset.export"), button -> {
            presetField.setValue(JumpDelayFix.exportCurrentPresetCode());
        }).bounds(left, rowY + (ROW_SPACING * 6), halfWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.preset.import"), button -> {
            boolean imported = JumpDelayFix.importPresetCode(presetField.getValue());
            NeoForgeStatusMessages.sendSimpleInfo(imported
                    ? "message.jumpdelayfix.preset.imported"
                    : "message.jumpdelayfix.preset.invalid"
            );
        }).bounds(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 6), halfWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> close())
                .bounds(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int panelRight = panelLeft + panelWidth;
        int panelBottom = panelTop + panelHeight;
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelBottom, 0xD0101726);
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 22, 0xD0243552);
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 1, 0xFF93DDFF);
        guiGraphics.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, 0xFF060A12);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, panelTop + 7, 0xBCEBFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.min_attempts", Integer.toString(config.minAttemptsForProfileSwitch())), panelLeft + 12, minAttemptsRowY + 6, 0xDDE7FF, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.comp_rate", Integer.toString((int) Math.round(config.competitiveRollbackRateMax() * 100.0D))), panelLeft + 12, competitiveRateRowY + 6, 0xDDE7FF, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.stable_rate", Integer.toString((int) Math.round(config.stableRollbackRateMin() * 100.0D))), panelLeft + 12, stableRateRowY + 6, 0xDDE7FF, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.failsafe_rate", Integer.toString((int) Math.round(config.failsafeRollbackRate() * 100.0D))), panelLeft + 12, failsafeRateRowY + 6, 0xDDE7FF, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.section.preset"), panelLeft + 12, presetField.getY() - 12, 0x95FFC9, false);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    private void close() {
        onClose();
    }

    private void addStepperButtons(int rowY, Runnable decreaseAction, Runnable increaseAction) {
        int minusX = panelLeft + panelWidth - PANEL_PADDING - 70;
        int plusX = panelLeft + panelWidth - PANEL_PADDING - 34;

        this.addRenderableWidget(Button.builder(Component.literal("-"), button -> decreaseAction.run())
                .bounds(minusX, rowY, 30, BUTTON_HEIGHT)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), button -> increaseAction.run())
                .bounds(plusX, rowY, 30, BUTTON_HEIGHT)
                .build());
    }
}
