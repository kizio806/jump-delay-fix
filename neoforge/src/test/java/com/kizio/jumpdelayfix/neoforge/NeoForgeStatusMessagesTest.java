package com.kizio.jumpdelayfix.neoforge;

import com.kizio.jumpdelayfix.model.JumpProfile;
import com.kizio.jumpdelayfix.neoforge.client.NeoForgeStatusMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NeoForgeStatusMessagesTest {

    @Test
    void shouldSafelyHandleToggleStatusWithoutClient() {
        assertDoesNotThrow(() -> NeoForgeStatusMessages.sendToggleStatus(true));
        assertDoesNotThrow(() -> NeoForgeStatusMessages.sendToggleStatus(false));
    }

    @Test
    void shouldSafelyHandleProfileStatusWithoutClient() {
        assertDoesNotThrow(() -> NeoForgeStatusMessages.sendProfileStatus(JumpProfile.SMART));
    }
}
