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

    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_SPACING = 24;

    private final Screen parent;

    private ButtonWidget toggleButton;
    private ButtonWidget profileButton;
    private ButtonWidget autoButton;

    public FabricSettingsScreen(Screen parent) {
        super(Text.translatable("screen.jumpdelayfix.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - (BUTTON_WIDTH / 2);
        int rowY = this.height / 4 + 32;

        toggleButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleEnabled();
            refreshButtonLabels();
        }).dimensions(left, rowY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        profileButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.cycleProfile();
            refreshButtonLabels();
            FabricStatusMessages.sendProfileStatus(JumpDelayFix.getProfile());
        }).dimensions(left, rowY + ROW_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        autoButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            JumpDelayFix.toggleAutoProfileSwitch();
            refreshButtonLabels();
        }).dimensions(left, rowY + (ROW_SPACING * 2), BUTTON_WIDTH, BUTTON_HEIGHT).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.jumpdelayfix.option.done"),
                button -> close()
        ).dimensions(left, rowY + (ROW_SPACING * 4), BUTTON_WIDTH, BUTTON_HEIGHT).build());

        refreshButtonLabels();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.jumpdelayfix.settings.subtitle"),
                centerX,
                34,
                0xA0A0A0
        );
        context.drawCenteredTextWithShadow(this.textRenderer, profileStatusText(), centerX, 48, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, autoStatusText(), centerX, 60, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("gui.jumpdelayfix.desc.profile"), centerX, this.height - 50, 0xA0A0A0);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("gui.jumpdelayfix.desc.auto"), centerX, this.height - 38, 0xA0A0A0);
    }

    @Override
    public void close() {
        JumpDelayFix.flushPendingConfiguration();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private void refreshButtonLabels() {
        toggleButton.setMessage(Text.translatable("gui.jumpdelayfix.toggle", booleanText(JumpDelayFix.isEnabled())));
        profileButton.setMessage(Text.literal(Text.translatable("gui.jumpdelayfix.profile.cycle").getString() + ": ")
                .append(Text.translatable(JumpDelayFix.getProfile().translationKey())));
        autoButton.setMessage(Text.translatable("gui.jumpdelayfix.auto", booleanText(JumpDelayFix.isAutoProfileSwitchEnabled())));
    }

    private Text profileStatusText() {
        return Text.translatable(
                "message.jumpdelayfix.profile_status",
                Text.translatable(JumpDelayFix.getProfile().translationKey())
        );
    }

    private Text autoStatusText() {
        return Text.translatable("gui.jumpdelayfix.auto", booleanText(JumpDelayFix.isAutoProfileSwitchEnabled()));
    }

    private Text booleanText(boolean enabled) {
        return enabled
                ? Text.translatable("message.jumpdelayfix.enabled")
                : Text.translatable("message.jumpdelayfix.disabled");
    }
}
