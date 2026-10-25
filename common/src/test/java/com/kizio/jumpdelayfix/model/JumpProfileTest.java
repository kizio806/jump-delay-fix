package com.kizio.jumpdelayfix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JumpProfileTest {

    @Test
    void shouldProvideCorrectTranslationKeysAndProfileProperties() {
        assertEquals("message.jumpdelayfix.profile.competitive", JumpProfile.COMPETITIVE.translationKey());
        assertEquals("message.jumpdelayfix.profile.smart", JumpProfile.SMART.translationKey());
        assertEquals("message.jumpdelayfix.profile.stable", JumpProfile.STABLE.translationKey());

        assertEquals(-1, JumpProfile.COMPETITIVE.groundedTicksOffset());
        assertEquals(0, JumpProfile.SMART.groundedTicksOffset());
        assertEquals(1, JumpProfile.STABLE.groundedTicksOffset());

        assertEquals(1, JumpProfile.COMPETITIVE.maxAdaptivePenaltyTicks());
        assertEquals(3, JumpProfile.SMART.maxAdaptivePenaltyTicks());
        assertEquals(5, JumpProfile.STABLE.maxAdaptivePenaltyTicks());

        assertEquals(5, JumpProfile.COMPETITIVE.successfulJumpsToReducePenalty());
        assertEquals(3, JumpProfile.SMART.successfulJumpsToReducePenalty());
        assertEquals(2, JumpProfile.STABLE.successfulJumpsToReducePenalty());
    }

    @Test
    void shouldCycleThroughAllProfilesInOrder() {
        assertEquals(JumpProfile.SMART, JumpProfile.COMPETITIVE.next());
        assertEquals(JumpProfile.STABLE, JumpProfile.SMART.next());
        assertEquals(JumpProfile.COMPETITIVE, JumpProfile.STABLE.next());
    }
}
