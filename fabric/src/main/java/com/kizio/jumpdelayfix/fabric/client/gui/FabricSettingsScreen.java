package com.kizio.jumpdelayfix.fabric.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.fabric.client.FabricStatusMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class FabricSettingsScreen extends Screen {

    private static final int MAX_PANEL_WIDTH = 430;
    private static final int MAX_PANEL_HEIGHT = 320;
    private static final int PANEL_PADDING = 12;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_SPACING = 22;
    private static final int BUTTON_GAP = 8;

    private final Screen parent;

    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;

    private ButtonWidget toggleButton;
    private ButtonWidget autoButton;
    private ButtonWidget profileCycleButton;
    private ButtonWidget shadowButton;
    private ButtonWidget failsafeButton;
    private ButtonWidget hudButton;

    public FabricSettingsScreen(Screen parent) {
        super(Text.translatable("screen.jumpdelayfix.settings.title"));
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

        toggleButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleEnabled();
            refreshButtonLabels();
        }).dimensions(left, rowY, contentWidth, BUTTON_HEIGHT).build());

        autoButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleAutoProfileSwitch();
            refreshButtonLabels();
        }).dimensions(left, rowY + ROW_SPACING, contentWidth, BUTTON_HEIGHT).build());

        profileCycleButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.cycleProfile();
            refreshButtonLabels();
            FabricStatusMessages.sendProfileStatus(JumpDelayFix.getProfile());
        }).dimensions(left, rowY + (ROW_SPACING * 2), contentWidth, BUTTON_HEIGHT).build());

        shadowButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleShadowMode();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 3), contentWidth, BUTTON_HEIGHT).build());

        failsafeButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleSafetyFailsafe();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 4), contentWidth, BUTTON_HEIGHT).build());

        hudButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudEnabled();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.open"), button -> {
            if (this.client != null) {
                this.client.setScreen(new FabricHudEditorScreen(this));
            }
        }).dimensions(left, rowY + (ROW_SPACING * 6), contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.section.adaptive"), button -> {
            if (this.client != null) {
                this.client.setScreen(new FabricAdvancedSettingsScreen(this));
            }
        }).dimensions(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.server_memory.clear"), button -> {
            JumpDelayFix.clearServerProfileMemory();
            refreshButtonLabels();
            FabricStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.saved");
        }).dimensions(left, rowY + (ROW_SPACING * 8), contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.reset_defaults"), button -> {
            JumpDelayFix.resetSettingsToDefaults();
            refreshButtonLabels();
            FabricStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.defaults");
        }).dimensions(left, rowY + (ROW_SPACING * 9), halfWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
                .dimensions(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 9), halfWidth, BUTTON_HEIGHT)
                .build());

        refreshButtonLabels();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int panelRight = panelLeft + panelWidth;
        int panelBottom = panelTop + panelHeight;
        context.fill(panelLeft, panelTop, panelRight, panelBottom, 0xD00F1623);
        context.fill(panelLeft, panelTop, panelRight, panelTop + 22, 0xD0254865);
        context.fill(panelLeft, panelTop, panelRight, panelTop + 1, 0xFF97EFFF);
        context.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, 0xFF060A12);

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelTop + 7, 0xB6FFE3);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.section.core"), panelLeft + 10, panelTop + 30, 0x95FFC9, false);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.stats",
                Text.translatable(JumpDelayFix.getProfile().translationKey()),
                Integer.toString(JumpDelayFix.getDiagnostics().latencyMs()),
                Integer.toString((int) Math.round(JumpDelayFix.getDiagnostics().rollbackRate() * 100.0D))
        ), panelLeft + 10, panelTop + 42, 0xDDE7FF, false);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private void refreshButtonLabels() {
        toggleButton.setMessage(Text.translatable("gui.jumpdelayfix.toggle", booleanText(JumpDelayFix.isEnabled())));
        autoButton.setMessage(Text.translatable("gui.jumpdelayfix.auto", booleanText(JumpDelayFix.isAutoProfileSwitchEnabled())));
        shadowButton.setMessage(Text.translatable("gui.jumpdelayfix.shadow", booleanText(JumpDelayFix.isShadowModeEnabled())));
        failsafeButton.setMessage(Text.translatable("gui.jumpdelayfix.failsafe", booleanText(JumpDelayFix.isSafetyFailsafeEnabled())));
        hudButton.setMessage(Text.translatable("gui.jumpdelayfix.hud", booleanText(JumpDelayFix.isHudEnabled())));
        profileCycleButton.setMessage(Text.translatable("gui.jumpdelayfix.profile.cycle")
                .copy()
                .append(Text.literal(": "))
                .append(Text.translatable(JumpDelayFix.getProfile().translationKey())));
    }

    private Text booleanText(boolean enabled) {
        return enabled
                ? Text.translatable("message.jumpdelayfix.enabled")
                : Text.translatable("message.jumpdelayfix.disabled");
    }
}
