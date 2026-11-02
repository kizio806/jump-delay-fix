package com.kizio.jumpdelayfix.fabric;

import com.kizio.jumpdelayfix.fabric.client.FabricStatusMessages;
import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FabricStatusMessagesTest {

    @Test
    void shouldSafelyHandleToggleStatusWithoutClient() {
        assertDoesNotThrow(() -> FabricStatusMessages.sendToggleStatus(true));
        assertDoesNotThrow(() -> FabricStatusMessages.sendToggleStatus(false));
    }

    @Test
    void shouldSafelyHandleProfileStatusWithoutClient() {
        assertDoesNotThrow(() -> FabricStatusMessages.sendProfileStatus(JumpProfile.SMART));
    }
}
