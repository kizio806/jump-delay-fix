package com.kizio.jumpdelayfix.state;

import com.kizio.jumpdelayfix.model.JumpProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeStateTest {

    @BeforeEach
    void setUp() {
        RuntimeState.reset();
    }

    @Test
    void shouldToggleEnabledState() {
        assertTrue(RuntimeState.isEnabled());

        boolean toggledOff = RuntimeState.toggle();
        assertFalse(toggledOff);
        assertFalse(RuntimeState.isEnabled());

        boolean toggledOn = RuntimeState.toggle();
        assertTrue(toggledOn);
        assertTrue(RuntimeState.isEnabled());
    }

    @Test
    void shouldSetExplicitEnabledState() {
        RuntimeState.setEnabled(false);
        assertFalse(RuntimeState.isEnabled());

        RuntimeState.setEnabled(true);
        assertTrue(RuntimeState.isEnabled());
    }

    @Test
    void shouldCycleProfilesInOrder() {
        assertEquals(JumpProfile.SMART, RuntimeState.getProfile());

        JumpProfile firstCycle = RuntimeState.cycleProfile();
        assertEquals(JumpProfile.SMART.next(), firstCycle);
        assertEquals(firstCycle, RuntimeState.getProfile());

        JumpProfile secondCycle = RuntimeState.cycleProfile();
        assertEquals(firstCycle.next(), secondCycle);

        JumpProfile thirdCycle = RuntimeState.cycleProfile();
        assertEquals(secondCycle.next(), thirdCycle);
        assertEquals(JumpProfile.SMART, thirdCycle);
    }

    @Test
    void shouldSetExplicitProfile() {
        RuntimeState.setProfile(JumpProfile.COMPETITIVE);
        assertEquals(JumpProfile.COMPETITIVE, RuntimeState.getProfile());

        RuntimeState.setProfile(JumpProfile.STABLE);
        assertEquals(JumpProfile.STABLE, RuntimeState.getProfile());
    }

    @Test
    void shouldRejectNullProfile() {
        assertThrows(NullPointerException.class, () -> RuntimeState.setProfile(null));
    }

    @Test
    void shouldResetToDefaults() {
        RuntimeState.setEnabled(false);
        RuntimeState.setProfile(JumpProfile.COMPETITIVE);

        RuntimeState.reset();

        assertTrue(RuntimeState.isEnabled());
        assertEquals(JumpProfile.SMART, RuntimeState.getProfile());
    }
}
