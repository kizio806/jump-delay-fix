package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.Constants;
import com.kizio.jumpdelayfix.input.KeyBindingProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class FabricKeyMappings implements KeyBindingProvider {

    private static final FabricKeyMappings INSTANCE = new FabricKeyMappings();
    private static final int DEFAULT_TOGGLE_KEY = GLFW.GLFW_KEY_J;
    private static final int DEFAULT_PROFILE_KEY = GLFW.GLFW_KEY_H;
    private static final int DEFAULT_CONFIG_KEY = GLFW.GLFW_KEY_O;
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(
            Identifier.of(Constants.MOD_ID, "jumpdelayfix")
    );
    private static KeyBinding toggleKey;
    private static KeyBinding profileKey;
    private static KeyBinding configKey;

    private FabricKeyMappings() {

    }

    public static FabricKeyMappings getInstance() {
        return INSTANCE;
    }

    public void register() {
        if (toggleKey != null) {
            return;
        }

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.toggle",
                InputUtil.Type.KEYSYM,
                DEFAULT_TOGGLE_KEY,
                KEY_CATEGORY
        ));

        profileKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.profile",
                InputUtil.Type.KEYSYM,
                DEFAULT_PROFILE_KEY,
                KEY_CATEGORY
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.jumpdelayfix.config",
                InputUtil.Type.KEYSYM,
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
        while (toggleKey.wasPressed()) {
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
        while (profileKey.wasPressed()) {
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
        while (configKey.wasPressed()) {
            pressed = true;
        }
        return pressed;
    }
}
