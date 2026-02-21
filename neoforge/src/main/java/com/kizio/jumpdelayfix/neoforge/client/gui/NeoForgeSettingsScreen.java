package com.kizio.jumpdelayfix.neoforge.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeStatusMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeSettingsScreen extends Screen {

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

    private Button toggleButton;
    private Button autoButton;
    private Button profileCycleButton;
    private Button shadowButton;
    private Button failsafeButton;
    private Button hudButton;

    public NeoForgeSettingsScreen(Screen parent) {
        super(Component.translatable("screen.jumpdelayfix.settings.title"));
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

        toggleButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleEnabled();
            refreshButtonLabels();
        }).bounds(left, rowY, contentWidth, BUTTON_HEIGHT).build());

        autoButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleAutoProfileSwitch();
            refreshButtonLabels();
        }).bounds(left, rowY + ROW_SPACING, contentWidth, BUTTON_HEIGHT).build());

        profileCycleButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.cycleProfile();
            refreshButtonLabels();
            NeoForgeStatusMessages.sendProfileStatus(JumpDelayFix.getProfile());
        }).bounds(left, rowY + (ROW_SPACING * 2), contentWidth, BUTTON_HEIGHT).build());

        shadowButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleShadowMode();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 3), contentWidth, BUTTON_HEIGHT).build());

        failsafeButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleSafetyFailsafe();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 4), contentWidth, BUTTON_HEIGHT).build());

        hudButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudEnabled();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.open"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new NeoForgeHudEditorScreen(this));
            }
        }).bounds(left, rowY + (ROW_SPACING * 6), contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.section.adaptive"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new NeoForgeAdvancedSettingsScreen(this));
            }
        }).bounds(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.server_memory.clear"), button -> {
            JumpDelayFix.clearServerProfileMemory();
            refreshButtonLabels();
            NeoForgeStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.saved");
        }).bounds(left, rowY + (ROW_SPACING * 8), contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.reset_defaults"), button -> {
            JumpDelayFix.resetSettingsToDefaults();
            refreshButtonLabels();
            NeoForgeStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.defaults");
        }).bounds(left, rowY + (ROW_SPACING * 9), halfWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> close())
                .bounds(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 9), halfWidth, BUTTON_HEIGHT)
                .build());

        refreshButtonLabels();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int panelRight = panelLeft + panelWidth;
        int panelBottom = panelTop + panelHeight;
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelBottom, 0xD00F1623);
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 22, 0xD0254865);
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 1, 0xFF97EFFF);
        guiGraphics.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, 0xFF060A12);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, panelTop + 7, 0xB6FFE3);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.section.core"), panelLeft + 10, panelTop + 30, 0x95FFC9, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.stats",
                Component.translatable(JumpDelayFix.getProfile().translationKey()),
                Integer.toString(JumpDelayFix.getDiagnostics().latencyMs()),
                Integer.toString((int) Math.round(JumpDelayFix.getDiagnostics().rollbackRate() * 100.0D))
        ), panelLeft + 10, panelTop + 42, 0xDDE7FF, false);
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

    private void refreshButtonLabels() {
        toggleButton.setMessage(Component.translatable("gui.jumpdelayfix.toggle", booleanComponent(JumpDelayFix.isEnabled())));
        autoButton.setMessage(Component.translatable("gui.jumpdelayfix.auto", booleanComponent(JumpDelayFix.isAutoProfileSwitchEnabled())));
        shadowButton.setMessage(Component.translatable("gui.jumpdelayfix.shadow", booleanComponent(JumpDelayFix.isShadowModeEnabled())));
        failsafeButton.setMessage(Component.translatable("gui.jumpdelayfix.failsafe", booleanComponent(JumpDelayFix.isSafetyFailsafeEnabled())));
        hudButton.setMessage(Component.translatable("gui.jumpdelayfix.hud", booleanComponent(JumpDelayFix.isHudEnabled())));
        profileCycleButton.setMessage(Component.translatable("gui.jumpdelayfix.profile.cycle")
                .copy()
                .append(Component.literal(": "))
                .append(Component.translatable(JumpDelayFix.getProfile().translationKey())));
    }

    private Component booleanComponent(boolean enabled) {
        return enabled
                ? Component.translatable("message.jumpdelayfix.enabled")
                : Component.translatable("message.jumpdelayfix.disabled");
    }
}
