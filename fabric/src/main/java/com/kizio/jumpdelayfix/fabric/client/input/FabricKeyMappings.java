package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.input.KeyBindingProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class FabricKeyMappings implements KeyBindingProvider {

    private static final FabricKeyMappings INSTANCE = new FabricKeyMappings();
    private static final int DEFAULT_TOGGLE_KEY = GLFW.GLFW_KEY_J;
    private static final int DEFAULT_PROFILE_KEY = GLFW.GLFW_KEY_H;
    private static final int DEFAULT_CONFIG_KEY = GLFW.GLFW_KEY_O;
    private static final String KEY_CATEGORY = "key.categories." + Constants.MOD_ID;
    
    private static KeyMapping toggleKey;
    private static KeyMapping profileKey;
    private static KeyMapping configKey;

    private FabricKeyMappings() {

    }

    public static FabricKeyMappings getInstance() {
        return INSTANCE;
    }

    public void register() {
        if (toggleKey != null) {
            return;
        }

        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jumpdelayfix.toggle",
                InputConstants.Type.KEYSYM,
                DEFAULT_TOGGLE_KEY,
                KEY_CATEGORY
        ));

        profileKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jumpdelayfix.profile",
                InputConstants.Type.KEYSYM,
                DEFAULT_PROFILE_KEY,
                KEY_CATEGORY
        ));

        configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jumpdelayfix.config",
                InputConstants.Type.KEYSYM,
                DEFAULT_CONFIG_KEY,
                KEY_CATEGORY
        ));
    }

    @Override
    public boolean consumeTogglePress() {
        if (toggleKey == null) {
            return false;
        }

        boolean pressed = false;
        while (toggleKey.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }

    @Override
    public boolean consumeProfileCyclePress() {
        if (profileKey == null) {
            return false;
        }

        boolean pressed = false;
        while (profileKey.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }

    @Override
    public boolean consumeConfigScreenPress() {
        if (configKey == null) {
            return false;
        }

        boolean pressed = false;
        while (configKey.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }
}
