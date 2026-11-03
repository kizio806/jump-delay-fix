package com.kizio.jumpdelayfix.fabric.client.input;

import com.kizio.jumpdelayfix.client.input.AbstractKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public final class FabricKeyMappings extends AbstractKeyMappings {

    private static final FabricKeyMappings INSTANCE = new FabricKeyMappings();

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
                InputConstants.Type.KEYBOARD,
                DEFAULT_TOGGLE_KEY,
                KEY_CATEGORY
        ));

        profileKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jumpdelayfix.profile",
                InputConstants.Type.KEYBOARD,
                DEFAULT_PROFILE_KEY,
                KEY_CATEGORY
        ));

        configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jumpdelayfix.config",
                InputConstants.Type.KEYBOARD,
                DEFAULT_CONFIG_KEY,
                KEY_CATEGORY
        ));
    }

    public boolean consumeTogglePress() {
        return consumePress(toggleKey);
    }

    public boolean consumeProfileCyclePress() {
        return consumePress(profileKey);
    }

    public boolean consumeConfigScreenPress() {
        return consumePress(configKey);
    }
}
