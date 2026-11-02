package com.kizio.jumpdelayfix.neoforge.client.gui;

import com.kizio.jumpdelayfix.client.gui.SettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeSettingsScreen extends SettingsScreen {

    public NeoForgeSettingsScreen(Screen parent) {
        super(parent);
    }
}
