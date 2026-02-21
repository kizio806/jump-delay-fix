package com.kizio.jumpdelayfix.fabric.client.gui;

import com.kizio.jumpdelayfix.common.JumpDelayFix;
import com.kizio.jumpdelayfix.common.config.JumpRuntimeConfig;
import com.kizio.jumpdelayfix.common.model.JumpDiagnostics;
import com.kizio.jumpdelayfix.fabric.client.FabricStatusMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class FabricHudEditorScreen extends Screen {

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

    private ButtonWidget hudToggleButton;
    private ButtonWidget profileLineButton;
    private ButtonWidget timingLineButton;
    private ButtonWidget modeLineButton;
    private ButtonWidget serverLineButton;
    private ButtonWidget qualityBarButton;

    private int previewHudX;
    private int previewHudY;
    private boolean draggingPreview;
    private int dragMouseStartX;
    private int dragMouseStartY;
    private int dragHudStartX;
    private int dragHudStartY;

    public FabricHudEditorScreen(Screen parent) {
        super(Text.translatable("screen.jumpdelayfix.hud_editor.title"));
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

        hudToggleButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudEnabled();
            refreshButtonLabels();
        }).dimensions(left, rowY, contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.move_up"), button -> nudgeHud(0, -MOVE_STEP))
                .dimensions(left, rowY + ROW_SPACING, halfWidth, BUTTON_HEIGHT)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.move_down"), button -> nudgeHud(0, MOVE_STEP))
                .dimensions(left + halfWidth + BUTTON_GAP, rowY + ROW_SPACING, halfWidth, BUTTON_HEIGHT)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.move_left"), button -> nudgeHud(-MOVE_STEP, 0))
                .dimensions(left, rowY + (ROW_SPACING * 2), halfWidth, BUTTON_HEIGHT)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.move_right"), button -> nudgeHud(MOVE_STEP, 0))
                .dimensions(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 2), halfWidth, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.scale_down"), button -> JumpDelayFix.adjustHudScale(-SCALE_STEP))
                .dimensions(left, rowY + (ROW_SPACING * 3), halfWidth, BUTTON_HEIGHT)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.scale_up"), button -> JumpDelayFix.adjustHudScale(SCALE_STEP))
                .dimensions(left + halfWidth + BUTTON_GAP, rowY + (ROW_SPACING * 3), halfWidth, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.jumpdelayfix.hud_editor.reset_layout"), button -> {
            JumpDelayFix.resetHudLayout();
            previewHudX = JumpDelayFix.getHudOffsetX();
            previewHudY = JumpDelayFix.getHudOffsetY();
            refreshButtonLabels();
            FabricStatusMessages.sendSimpleInfo("message.jumpdelayfix.settings.saved");
        }).dimensions(left, rowY + (ROW_SPACING * 4), contentWidth, BUTTON_HEIGHT).build());

        profileLineButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudProfileAndPing();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 5), contentWidth, BUTTON_HEIGHT).build());

        timingLineButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudRollbackAndPenalty();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 6), contentWidth, BUTTON_HEIGHT).build());

        modeLineButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudModeAndQuality();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 7), contentWidth, BUTTON_HEIGHT).build());

        serverLineButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudServer();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 8), contentWidth, BUTTON_HEIGHT).build());

        qualityBarButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleHudQualityBar();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 9), contentWidth, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.back"), button -> close())
                .dimensions(left, rowY + (ROW_SPACING * 10), contentWidth, BUTTON_HEIGHT)
                .build());

        refreshButtonLabels();
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
        JumpDiagnostics diagnostics = JumpDelayFix.getDiagnostics();

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelTop + 7, 0xBCEBFF);
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.hud_editor.description"), panelLeft + 10, panelTop + 30, 0xD9E7FF, false);
        context.drawText(this.textRenderer,
                Text.translatable("gui.jumpdelayfix.hud_editor.current_position",
                        Integer.toString(previewHudX),
                        Integer.toString(previewHudY),
                        Integer.toString((int) Math.round(config.hudScale() * 100.0D))
                ),
                panelLeft + 10,
                panelTop + 42,
                0xAFC6E7,
                false
        );
        context.drawText(this.textRenderer, Text.translatable("gui.jumpdelayfix.hud_editor.drag_hint"), panelLeft + 10, panelTop + 54, 0xAFC6E7, false);

        drawHudPreview(context, diagnostics, config);
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
    public void close() {
        persistPreviewPosition();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private void refreshButtonLabels() {
        hudToggleButton.setMessage(Text.translatable("gui.jumpdelayfix.hud", booleanText(JumpDelayFix.isHudEnabled())));
        profileLineButton.setMessage(Text.translatable("gui.jumpdelayfix.hud_editor.toggle_profile", booleanText(JumpDelayFix.isHudProfileAndPingVisible())));
        timingLineButton.setMessage(Text.translatable("gui.jumpdelayfix.hud_editor.toggle_timing", booleanText(JumpDelayFix.isHudRollbackAndPenaltyVisible())));
        modeLineButton.setMessage(Text.translatable("gui.jumpdelayfix.hud_editor.toggle_mode", booleanText(JumpDelayFix.isHudModeAndQualityVisible())));
        serverLineButton.setMessage(Text.translatable("gui.jumpdelayfix.hud_editor.toggle_server", booleanText(JumpDelayFix.isHudServerVisible())));
        qualityBarButton.setMessage(Text.translatable("gui.jumpdelayfix.hud_editor.toggle_bar", booleanText(JumpDelayFix.isHudQualityBarVisible())));
    }

    private Text booleanText(boolean enabled) {
        return enabled
                ? Text.translatable("message.jumpdelayfix.enabled")
                : Text.translatable("message.jumpdelayfix.disabled");
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

    private void drawHudPreview(DrawContext context, JumpDiagnostics diagnostics, JumpRuntimeConfig config) {
        int rollbackPercent = (int) Math.round(Math.min(1.0D, Math.max(0.0D, diagnostics.rollbackRate())) * 100.0D);
        int qualityColor = qualityColor(diagnostics.rollbackRate());
        int visibleLines = countVisibleLines(config);
        int panelHeight = 26 + (visibleLines * 11) + (config.hudShowQualityBar() ? 11 : 0);

        context.getMatrices().push();
        context.getMatrices().translate(previewHudX, previewHudY, 0.0F);
        context.getMatrices().scale((float) config.hudScale(), (float) config.hudScale(), 1.0F);

        context.fill(-3, -3, HUD_PANEL_WIDTH, panelHeight, 0xA0000000);
        context.fill(-3, -3, HUD_PANEL_WIDTH, -2, 0xFF56D8FF);
        context.drawText(this.textRenderer, Text.literal("JumpDelayFix"), HUD_TEXT_X, 5, 0x80FF9D, true);

        int y = 16;
        if (config.hudShowProfileAndPing()) {
            context.drawText(this.textRenderer,
                    Text.literal("Profile: " + diagnostics.profile().name() + "  Ping: " + diagnostics.latencyMs() + "ms"),
                    HUD_TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowRollbackAndPenalty()) {
            context.drawText(this.textRenderer,
                    Text.literal("Rollback: " + rollbackPercent + "%  Penalty: " + diagnostics.adaptivePenaltyTicks() + "  ReqTicks: " + diagnostics.requiredGroundedTicks()),
                    HUD_TEXT_X,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 11;
        }
        if (config.hudShowModeAndQuality()) {
            context.drawText(this.textRenderer,
                    Text.literal("Mode: " + (diagnostics.shadowMode() ? "Shadow" : "Active")
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
            context.drawText(this.textRenderer,
                    Text.literal("Server: " + trimServerId(diagnostics.serverId())),
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

            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x50000000);
            if (filledWidth > 0) {
                context.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF000000 | qualityColor);
            }
        }

        context.getMatrices().pop();
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
