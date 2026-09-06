package com.kizio.jumpdelayfix.neoforge.client.gui;

import com.kizio.jumpdelayfix.JumpDelayFix;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeStatusMessages;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeSettingsScreen extends Screen {

    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_SPACING = 24;

    private final Screen parent;

    private Button toggleButton;
    private Button profileButton;
    private Button autoButton;

    public NeoForgeSettingsScreen(Screen parent) {
        super(Component.translatable("screen.jumpdelayfix.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - (BUTTON_WIDTH / 2);
        int rowY = this.height / 4 + 32;

        toggleButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleEnabled();
            refreshButtonLabels();
        }).bounds(left, rowY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        profileButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.cycleProfile();
            refreshButtonLabels();
            NeoForgeStatusMessages.sendProfileStatus(JumpDelayFix.getProfile());
        }).bounds(left, rowY + ROW_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        autoButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            JumpDelayFix.toggleAutoProfileSwitch();
            refreshButtonLabels();
        }).bounds(left, rowY + (ROW_SPACING * 2), BUTTON_WIDTH, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.jumpdelayfix.option.done"),
                button -> onClose()
        ).bounds(left, rowY + (ROW_SPACING * 4), BUTTON_WIDTH, BUTTON_HEIGHT).build());

        refreshButtonLabels();
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        extractor.centeredText(this.font, this.title, centerX, 20, 0xFFFFFF);
        extractor.centeredText(this.font, profileStatusText(), centerX, 48, 0xFFFFFF);
        extractor.centeredText(this.font, autoStatusText(), centerX, 60, 0xFFFFFF);
        extractor.centeredText(this.font, Component.translatable("gui.jumpdelayfix.desc.profile"), centerX, this.height - 50, 0xA0A0A0);
        extractor.centeredText(this.font, Component.translatable("gui.jumpdelayfix.desc.auto"), centerX, this.height - 38, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        JumpDelayFix.flushPendingConfiguration();
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    private void refreshButtonLabels() {
        toggleButton.setMessage(Component.translatable("gui.jumpdelayfix.toggle", booleanComponent(JumpDelayFix.isEnabled())));
        profileButton.setMessage(Component.literal(Component.translatable("gui.jumpdelayfix.profile.cycle").getString() + ": ")
                .append(Component.translatable(JumpDelayFix.getProfile().translationKey())));
        autoButton.setMessage(Component.translatable("gui.jumpdelayfix.auto", booleanComponent(JumpDelayFix.isAutoProfileSwitchEnabled())));
    }

    private Component profileStatusText() {
        return Component.translatable(
                "message.jumpdelayfix.profile_status",
                Component.translatable(JumpDelayFix.getProfile().translationKey())
        );
    }

    private Component autoStatusText() {
        return Component.translatable("gui.jumpdelayfix.auto", booleanComponent(JumpDelayFix.isAutoProfileSwitchEnabled()));
    }

    private Component booleanComponent(boolean enabled) {
        return enabled
                ? Component.translatable("message.jumpdelayfix.enabled")
                : Component.translatable("message.jumpdelayfix.disabled");
    }
}
