package com.kizio.jumpdelayfix.neoforge.client.input;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeKeyMappings {

    private static final NeoForgeKeyMappings INSTANCE = new NeoForgeKeyMappings();
    private static final int DEFAULT_TOGGLE_KEY = GLFW.GLFW_KEY_J;
    private static final int DEFAULT_PROFILE_KEY = GLFW.GLFW_KEY_H;
    private static final int DEFAULT_CONFIG_KEY = GLFW.GLFW_KEY_O;
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.MISC;

    private static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.jumpdelayfix.toggle",
            DEFAULT_TOGGLE_KEY,
            KEY_CATEGORY
    );
    private static final KeyMapping PROFILE_KEY = new KeyMapping(
            "key.jumpdelayfix.profile",
            DEFAULT_PROFILE_KEY,
            KEY_CATEGORY
    );
    private static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.jumpdelayfix.config",
            DEFAULT_CONFIG_KEY,
            KEY_CATEGORY
    );

    private NeoForgeKeyMappings() {

    }

    public static NeoForgeKeyMappings getInstance() {
        return INSTANCE;
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
        event.register(PROFILE_KEY);
        event.register(CONFIG_KEY);
    }

    public boolean consumeTogglePress() {
        return TOGGLE_KEY.consumeClick();
    }

    public boolean consumeProfileCyclePress() {
        return PROFILE_KEY.consumeClick();
    }

    public boolean consumeConfigScreenPress() {
        return CONFIG_KEY.consumeClick();
    }
}
