package com.kizio.jumpdelayfix.state;

import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeStateTest {

    @AfterEach
    void tearDown() {
        RuntimeState.setEnabled(true);
        RuntimeState.setProfile(JumpProfile.SMART);
    }

    @Test
    void shouldToggleAndSetEnabledState() {
        RuntimeState.setEnabled(true);
        assertTrue(RuntimeState.isEnabled());

        assertFalse(RuntimeState.toggle());
        assertFalse(RuntimeState.isEnabled());

        assertTrue(RuntimeState.toggle());
        assertTrue(RuntimeState.isEnabled());

        RuntimeState.setEnabled(false);
        assertFalse(RuntimeState.isEnabled());
    }

    @Test
    void shouldCycleAndSetProfile() {
        RuntimeState.setProfile(JumpProfile.SMART);
        assertEquals(JumpProfile.SMART, RuntimeState.getProfile());

        assertEquals(JumpProfile.STABLE, RuntimeState.cycleProfile());
        assertEquals(JumpProfile.COMPETITIVE, RuntimeState.cycleProfile());
        assertEquals(JumpProfile.SMART, RuntimeState.cycleProfile());

        RuntimeState.setProfile(JumpProfile.COMPETITIVE);
        assertEquals(JumpProfile.COMPETITIVE, RuntimeState.getProfile());
    }
}
