package com.kizio.jumpdelayfix.fabric.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class FabricKeyMappings {

    private static final int DEFAULT_TOGGLE_KEY = GLFW.GLFW_KEY_J;
    private static final int DEFAULT_PROFILE_KEY = GLFW.GLFW_KEY_H;
    private static final int DEFAULT_CONFIG_KEY = GLFW.GLFW_KEY_O;
    private static KeyBinding toggleKey;
    private static KeyBinding profileKey;
    private static KeyBinding configKey;

    private FabricKeyMappings() {
    }

    public static void register() {
        if (toggleKey != null) {
            return;
        }

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.toggle",
                InputUtil.Type.KEYSYM,
                DEFAULT_TOGGLE_KEY,
                "category.jumpdelayfix"
        ));

        profileKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.profile",
                InputUtil.Type.KEYSYM,
                DEFAULT_PROFILE_KEY,
                "category.jumpdelayfix"
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.config",
                InputUtil.Type.KEYSYM,
                DEFAULT_CONFIG_KEY,
                "category.jumpdelayfix"
        ));
    }

    public static boolean consumeTogglePress() {
        if (toggleKey == null) {
            return false;
        }

        boolean pressed = false;
        while (toggleKey.wasPressed()) {
            pressed = true;
        }
        return pressed;
    }

    public static boolean consumeProfileCyclePress() {
        if (profileKey == null) {
            return false;
        }

        boolean pressed = false;
        while (profileKey.wasPressed()) {
            pressed = true;
        }
        return pressed;
    }

    public static boolean consumeConfigScreenPress() {
        if (configKey == null) {
            return false;
        }

        boolean pressed = false;
        while (configKey.wasPressed()) {
            pressed = true;
        }
        return pressed;
    }
}
