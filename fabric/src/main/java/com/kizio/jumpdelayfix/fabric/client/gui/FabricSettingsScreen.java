package com.kizio.jumpdelayfix.fabric.client.gui;

import com.kizio.jumpdelayfix.client.gui.SettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public final class FabricSettingsScreen extends SettingsScreen {

    public FabricSettingsScreen(Screen parent) {
        super(parent);
    }
}
