package com.kizio.jumpdelayfix.client.input;

import com.kizio.jumpdelayfix.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.sdl.SDLKeycode;

/**
 * Base helper for key mapping definitions, categories, and click consumption across mod loaders.
 */
public abstract class AbstractKeyMappings {

    public static final int DEFAULT_TOGGLE_KEY = SDLKeycode.SDLK_J;
    public static final int DEFAULT_PROFILE_KEY = SDLKeycode.SDLK_H;
    public static final int DEFAULT_CONFIG_KEY = SDLKeycode.SDLK_O;

    public static final KeyMapping.Category KEY_CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "jumpdelayfix"));

    protected static boolean consumePress(KeyMapping mapping) {
        if (mapping == null) {
            return false;
        }
        boolean pressed = false;
        while (mapping.consumeClick()) {
            pressed = true;
        }
        return pressed;
    }
}
