package com.kizio.jumpdelayfix.neoforge.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeStatusMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeHudEditorScreen extends Screen {

    private static final int MAX_PANEL_WIDTH = 430;
    private static final int MAX_PANEL_HEIGHT = 338;
    private static final int PANEL_PADDING = 12;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_SPACING = 22;
    private static final int BUTTON_GAP = 8;
    private static final int MOVE_STEP = 6;
    private static final double SCALE_STEP = 0.05D;

    private static final int HUD_PANEL_WIDTH = 250;
    private static final int HUD_TEXT_X = 6;

    private final Screen parent;

    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;

    private Button hudToggleButton;
    private Button profileLineButton;
    private Button timingLineButton;
    private Button modeLineButton;
    private Button serverLineButton;
    private Button qualityBarButton;

    private int previewHudX;
    private int previewHudY;
    private boolean draggingPreview;
    private int dragMouseStartX;
    private int dragMouseStartY;
    private int dragHudStartX;
    private int dragHudStartY;

    public NeoForgeHudEditorScreen(Screen parent) {
        super(Component.translatable("screen.jumpdelayfix.hud_editor.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        panelWidth = Math.min(MAX_PANEL_WIDTH, this.width - 16);
        panelHeight = Math.min(MAX_PANEL_HEIGHT, this.height - 16);
        panelLeft = (this.width - panelWidth) / 2;
        panelTop = (this.height - panelHeight) / 2;

        previewHudX = JumpDelayFix.getHudOffsetX();
        previewHudY = JumpDelayFix.getHudOffsetY();

        int left = panelLeft + PANEL_PADDING;
        int contentWidth = panelWidth - (PANEL_PADDING * 2);
        int halfWidth = (contentWidth - BUTTON_GAP) / 2;
        int rowY = panelTop + 78;

        hudToggleButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudEnabled();
            refreshButtonLabels();
        }).bounds(left, rowY, contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.move_up"), button -> nudgeHud(0, -MOVE_STEP))
                .bounds(left, rowY + ROW_SPACING, halfWidth, BUTTON_HEIGHT)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.move_down"), button -> nudgeHud(0, MOVE_STEP))
                .bounds(left + halfWidth + BUTTON_GAP, rowY + ROW_SPACING, halfWidth, BUTTON_HEIGHT)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.move_left"), button -> nudgeHud(-MOVE_STEP, 0))
                .bounds(left, rowY + (ROW_SPACING * 2), halfWidth, BUTTON_HEIGHT)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.move_right"), button -> nudgeHud(MOVE_STEP, 0))
                .bounds(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 2), halfWidth, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.scale_down"), button -> JumpDelayFix.adjustHudScale(-SCALE_STEP))
                .bounds(left, rowY + (ROW_SPACING * 3), halfWidth, BUTTON_HEIGHT)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.scale_up"), button -> JumpDelayFix.adjustHudScale(SCALE_STEP))
                .bounds(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 3), halfWidth, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.jumpdelayfix.hud_editor.reset_layout"), button -> {
            JumpDelayFix.resetHudLayout();
            previewHudX = JumpDelayFix.getHudOffsetX();
            previewHudY = JumpDelayFix.getHudOffsetY();
            refreshButtonLabels();
            NeoForgeStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.saved");
        }).bounds(left, rowY + (ROW_SPACING * 4), contentWidth, BUTTON_HEIGHT).build());

        profileLineButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudProfileAndPing();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT).build());

        timingLineButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudRollbackAndPenalty();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 6), contentWidth, BUTTON_HEIGHT).build());

        modeLineButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudModeAndQuality();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT).build());

        serverLineButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudServer();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 8), contentWidth, BUTTON_HEIGHT).build());

        qualityBarButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleHudQualityBar();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 9), contentWidth, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> close())
                .bounds(left, rowY + (ROW_SPACING * 10), contentWidth, BUTTON_HEIGHT)
                .build());

        refreshButtonLabels();
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
        JumpDiagnostics diagnostics = JumpDelayFix.getDiagnostics();

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, panelTop + 7, 0xBCEBFF);
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.hud_editor.description"), panelLeft + 10, panelTop + 30, 0xD9E7FF, false);
        guiGraphics.drawString(this.font,
                Component.translatable("gui.jumpdelayfix.hud_editor.current_position",
                        Integer.toString(previewHudX),
                        Integer.toString(previewHudY),
                        Integer.toString((int) Math.round(config.hudScale() * 100.0D))
                ),
                panelLeft + 10,
                panelTop + 42,
                0xAFC6E7,
                false
        );
        guiGraphics.drawString(this.font, Component.translatable("gui.jumpdelayfix.hud_editor.drag_hint"), panelLeft + 10, panelTop + 54, 0xAFC6E7, false);

        drawHudPreview(guiGraphics, diagnostics, config);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverPreview(mouseX, mouseY)) {
            draggingPreview = true;
            dragMouseStartX = (int) Math.round(mouseX);
            dragMouseStartY = (int) Math.round(mouseY);
            dragHudStartX = previewHudX;
            dragHudStartY = previewHudY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingPreview && button == 0) {
            int movedX = (int) Math.round(mouseX) - dragMouseStartX;
            int movedY = (int) Math.round(mouseY) - dragMouseStartY;
            previewHudX = dragHudStartX + movedX;
            previewHudY = dragHudStartY + movedY;
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingPreview && button == 0) {
            draggingPreview = false;
            persistPreviewPosition();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        persistPreviewPosition();
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    private void close() {
        onClose();
    }

    private void refreshButtonLabels() {
        hudToggleButton.setMessage(Component.translatable("gui.jumpdelayfix.hud", booleanComponent(JumpDelayFix.isHudEnabled())));
        profileLineButton.setMessage(Component.translatable("gui.jumpdelayfix.hud_editor.toggle_profile", booleanComponent(JumpDelayFix.isHudProfileAndPingVisible())));
        timingLineButton.setMessage(Component.translatable("gui.jumpdelayfix.hud_editor.toggle_timing", booleanComponent(JumpDelayFix.isHudRollbackAndPenaltyVisible())));
        modeLineButton.setMessage(Component.translatable("gui.jumpdelayfix.hud_editor.toggle_mode", booleanComponent(JumpDelayFix.isHudModeAndQualityVisible())));
        serverLineButton.setMessage(Component.translatable("gui.jumpdelayfix.hud_editor.toggle_server", booleanComponent(JumpDelayFix.isHudServerVisible())));
        qualityBarButton.setMessage(Component.translatable("gui.jumpdelayfix.hud_editor.toggle_bar", booleanComponent(JumpDelayFix.isHudQualityBarVisible())));
    }

    private Component booleanComponent(boolean enabled) {
        return enabled
                ? Component.translatable("message.jumpdelayfix.enabled")
                : Component.translatable("message.jumpdelayfix.disabled");
    }

    private void nudgeHud(int offsetX, int offsetY) {
        previewHudX += offsetX;
        previewHudY += offsetY;
        persistPreviewPosition();
    }

    private void persistPreviewPosition() {
        JumpDelayFix.setHudPosition(previewHudX, previewHudY);
        previewHudX = JumpDelayFix.getHudOffsetX();
        previewHudY = JumpDelayFix.getHudOffsetY();
    }

    private void drawHudPreview(GuiGraphics guiGraphics, JumpDiagnostics diagnostics, JumpRuntimeConfig config) {
        int rollbackPercent = (int) Math.round(Math.min(1.0D, Math.max(0.0D, diagnostics.rollbackRate())) * 100.0D);
        int qualityColor = qualityColor(diagnostics.rollbackRate());
        int visibleLines = countVisibleLines(config);
        int panelHeight = 26 + (visibleLines * 11) + (config.hudShowQualityBar() ? 11 : 0);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(previewHudX, previewHudY, 0.0F);
        guiGraphics.pose().scale((float) config.hudScale(), (float) config.hudScale(), 1.0F);

        guiGraphics.fill(-3, -3, HUD_PANEL_WIDTH, panelHeight, 0xA0000000);
        guiGraphics.fill(-3, -3, HUD_PANEL_WIDTH, -2, 0xFF56D8FF);
        guiGraphics.drawString(this.font, Component.literal("JumpDelayFix"), HUD_TEXT_X, 5, 0x80FF9D, true);

        int y = 16;
        if (config.hudShowProfileAndPing()) {
            guiGraphics.drawString(this.font,
                    Component.literal("Profile: " + diagnostics.profile().name() + "  Ping: " + diagnostics.latencyMs() + "ms"),
                    HUD_TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowRollbackAndPenalty()) {
            guiGraphics.drawString(this.font,
                    Component.literal("Rollback: " + rollbackPercent + "%  Penalty: " + diagnostics.adaptivePenaltyTicks() + "  ReqTicks: " + diagnostics.requiredGroundedTicks()),
                    HUD_TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowModeAndQuality()) {
            guiGraphics.drawString(this.font,
                    Component.literal("Mode: " + (diagnostics.shadowMode() ? "Shadow" : "Active")
                            + "  Auto: " + diagnostics.autoProfileSwitch()
                            + "  Quality: " + qualityLabel(diagnostics.rollbackRate())),
                    HUD_TEXT_X,
                    y,
                    diagnostics.shadowMode() ? 0xFFD27F : qualityColor,
                    false
            );
            y += 11;
        }
        if (config.hudShowServer()) {
            guiGraphics.drawString(this.font,
                    Component.literal("Server: " + trimServerId(diagnostics.serverId())),
                    HUD_TEXT_X,
                    y,
                    0x9ED0FF,
                    false
            );
            y += 11;
        }

        if (config.hudShowQualityBar()) {
            int barX = HUD_TEXT_X;
            int barY = y + 2;
            int barWidth = HUD_PANEL_WIDTH - 16;
            int barHeight = 6;
            int filledWidth = (int) Math.round((rollbackPercent / 100.0D) * barWidth);

            guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
            if (filledWidth > 0) {
                guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | qualityColor);
            }
        }

        guiGraphics.pose().popPose();
    }

    private boolean isOverPreview(double mouseX, double mouseY) {
        JumpRuntimeConfig config = JumpDelayFix.getRuntimeConfig();
        int visibleLines = countVisibleLines(config);
        int baseHeight = 26 + (visibleLines * 11) + (config.hudShowQualityBar() ? 11 : 0);
        int scaledWidth = (int) Math.ceil(HUD_PANEL_WIDTH * config.hudScale());
        int scaledHeight = (int) Math.ceil(baseHeight * config.hudScale());

        return mouseX >= previewHudX
                && mouseY >= previewHudY
                && mouseX <= previewHudX + scaledWidth
                && mouseY <= previewHudY + scaledHeight;
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
