package com.kizio.jumpdelayfix.fabric.client;

import com.kizio.jumpdelayfix.client.StatusMessages;
import com.kizio.jumpdelayfix.model.JumpProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class FabricStatusMessages {

    private FabricStatusMessages() {
    }

    public static void sendToggleStatus(boolean enabled) {
        StatusMessages.sendToggleStatus(enabled);
    }

    public static void sendProfileStatus(JumpProfile profile) {
        StatusMessages.sendProfileStatus(profile);
    }
}
