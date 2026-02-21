package com.kizio.jumpdelayfix.fabric.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.fabric.client.FabricStatusMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class FabricAdvancedSettingsScreen extends Screen {

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

    private TextFieldWidget presetField;

    public FabricAdvancedSettingsScreen(Screen parent) {
        super(Text.translatable("gui.jumpdelayfix.section.adaptive"));
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

        presetField = new TextFieldWidget(this.textRenderer, left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT, Text.translatable("gui.jumpdelayfix.preset"));
        presetField.setMaxLength(512);
        this.addDrawableChild(presetField);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.preset.export"), button -> {
            presetField.setText(JumpDelayFix.exportCurrentPresetCode());
        }).dimensions(left, rowY + (ROW_SPACING * 6), halfWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.preset.import"), button -> {
            boolean imported = JumpDelayFix.importPresetCode(presetField.getText());
            FabricStatusMessages.sendSimpleInfo(imported
                    ? "message.jumpdelayfix.preset.imported"
                    : "message.jumpdelayfix.preset.invalid"
            );
        }).dimensions(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 6), halfWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.back"), button -> close())
                .dimensions(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int panelRight = panelLeft + panelWidth;
        int panelBottom = panelTop + panelHeight;
        context.fill(panelLeft, panelTop, panelRight, panelBottom, 0xD0101726);
        context.fill(panelLeft, panelTop, panelRight, panelTop + 22, 0xD0243552);
        context.fill(panelLeft, panelTop, panelRight, panelTop + 1, 0xFF93DDFF);
        context.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, 0xFF060A12);

        super.render(context, mouseX, mouseY, delta);

        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelTop + 7, 0xBCEBFF);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.min_attempts", Integer.toString(config.minAttemptsForProfileSwitch())), panelLeft + 12, minAttemptsRowY + 6, 0xDDE7FF, false);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.comp_rate", Integer.toString((int) Math.round(config.competitiveRollbackRateMax() * 100.0D))), panelLeft + 12, competitiveRateRowY + 6, 0xDDE7FF, false);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.stable_rate", Integer.toString((int) Math.round(config.stableRollbackRateMin() * 100.0D))), panelLeft + 12, stableRateRowY + 6, 0xDDE7FF, false);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.failsafe_rate", Integer.toString((int) Math.round(config.failsafeRollbackRate() * 100.0D))), panelLeft + 12, failsafeRateRowY + 6, 0xDDE7FF, false);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.section.preset"), panelLeft + 12, presetField.getY() - 12, 0x95FFC9, false);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private void addStepperButtons(int rowY, Runnable decreaseAction, Runnable increaseAction) {
        int minusX = panelLeft + panelWidth - PANEL_PADDING - 70;
        int plusX = panelLeft + panelWidth - PANEL_PADDING - 34;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("-"), button -> decreaseAction.run())
                .dimensions(minusX, rowY, 30, BUTTON_HEIGHT)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> increaseAction.run())
                .dimensions(plusX, rowY, 30, BUTTON_HEIGHT)
                .build());
    }
}
