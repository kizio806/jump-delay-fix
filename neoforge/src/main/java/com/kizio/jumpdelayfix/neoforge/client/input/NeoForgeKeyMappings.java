package com.kizio.jumpdelayfix.neoforge.client.input;

import com.kizio.jumpdelayfix.common.api.IKeyBindingProvider;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeKeyMappings implements IKeyBindingProvider {

    private static final NeoForgeKeyMappings INSTANCE = new NeoForgeKeyMappings();
    private static final int DEFAULT_TOGGLE_KEY = GLFW.GLFW_KEY_J;
    private static final int DEFAULT_PROFILE_KEY = GLFW.GLFW_KEY_H;
    private static final int DEFAULT_CONFIG_KEY = GLFW.GLFW_KEY_O;
    private static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.jumpdelayfix.toggle",
            DEFAULT_TOGGLE_KEY,
            "category.jumpdelayfix"
    );
    private static final KeyMapping PROFILE_KEY = new KeyMapping(
            "key.jumpdelayfix.profile",
            DEFAULT_PROFILE_KEY,
            "category.jumpdelayfix"
    );
    private static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.jumpdelayfix.config",
            DEFAULT_CONFIG_KEY,
            "category.jumpdelayfix"
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

    @Override
    public boolean consumeTogglePress() {
        boolean pressed = false;
        while (TOGGLE_KEY.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }

    @Override
    public boolean consumeProfileCyclePress() {
        boolean pressed = false;
        while (PROFILE_KEY.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }

    @Override
    public boolean consumeConfigScreenPress() {
        boolean pressed = false;
        while (CONFIG_KEY.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }
}
