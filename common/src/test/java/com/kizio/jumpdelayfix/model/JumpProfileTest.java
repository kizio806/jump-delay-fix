package com.kizio.jumpdelayfix.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void nextMustVisitEveryProfileExactlyOnceAndReturnToStart() {
        JumpProfile[] allValues = JumpProfile.values();
        EnumSet<JumpProfile> visited = EnumSet.noneOf(JumpProfile.class);
        JumpProfile current = JumpProfile.COMPETITIVE;

        for (int i = 0; i < allValues.length; i++) {
            assertTrue(visited.add(current),
                    "next() revisited " + current + " before completing the full cycle");
            current = current.next();
        }

        assertEquals(JumpProfile.COMPETITIVE, current,
                "next() must return to the starting profile after visiting all profiles");
        assertEquals(allValues.length, visited.size(),
                "next() must visit every enum constant exactly once per cycle");
    }

    @Test
    void successfulJumpsToReducePenaltyMustBePositiveForAllProfiles() {
        for (JumpProfile profile : JumpProfile.values()) {
            assertTrue(profile.successfulJumpsToReducePenalty() > 0,
                    profile + ".successfulJumpsToReducePenalty() must be positive to allow penalty reduction");
        }
    }

    @Test
    void maxAdaptivePenaltyTicksMustBePositiveForAllProfiles() {
        for (JumpProfile profile : JumpProfile.values()) {
            assertTrue(profile.maxAdaptivePenaltyTicks() > 0,
                    profile + ".maxAdaptivePenaltyTicks() must be positive to enforce a real upper bound");
        }
    }
}
