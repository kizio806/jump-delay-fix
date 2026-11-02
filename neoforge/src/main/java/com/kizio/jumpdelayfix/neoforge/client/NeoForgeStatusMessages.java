package com.kizio.jumpdelayfix.neoforge.client;

import com.kizio.jumpdelayfix.client.StatusMessages;
import com.kizio.jumpdelayfix.model.JumpProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NeoForgeStatusMessages {

    private NeoForgeStatusMessages() {
    }

    public static void sendToggleStatus(boolean enabled) {
        StatusMessages.sendToggleStatus(enabled);
    }

    public static void sendProfileStatus(JumpProfile profile) {
        StatusMessages.sendProfileStatus(profile);
    }
}
