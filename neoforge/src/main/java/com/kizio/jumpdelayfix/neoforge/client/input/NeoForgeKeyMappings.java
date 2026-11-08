package com.kizio.jumpdelayfix.neoforge.client.input;

import com.kizio.jumpdelayfix.client.input.AbstractKeyMappings;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeKeyMappings extends AbstractKeyMappings {

    private static final NeoForgeKeyMappings INSTANCE = new NeoForgeKeyMappings();

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
        event.registerCategory(KEY_CATEGORY);
        event.register(TOGGLE_KEY);
        event.register(PROFILE_KEY);
        event.register(CONFIG_KEY);
    }

    @Override
    public boolean consumeTogglePress() {
        return consumePress(TOGGLE_KEY);
    }

    @Override
    public boolean consumeProfileCyclePress() {
        return consumePress(PROFILE_KEY);
    }

    @Override
    public boolean consumeConfigScreenPress() {
        return consumePress(CONFIG_KEY);
    }
}
