package com.kizio.jumpdelayfix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerAdaptiveStatsTest {

    @Test
    void shouldAccumulateCountersAfterUpdate() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();

        stats.update(3, 1);

        assertEquals(3, stats.confirmedJumps());
        assertEquals(1, stats.rejectedJumps());
    }

    @Test
    void rollbackRateShouldBeExactlyZeroBeforeAnyUpdate() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        assertEquals(0.0D, stats.rollbackRate(), "Initial rollback rate must be exactly zero");
    }

    @Test
    void rollbackRateShouldReflectEWMASmoothingFormula() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();

        // batch: 3 confirmed + 1 rejected → batchRollbackRate = 1/4 = 0.25
        // smoothed = 0.0 * 0.82 + 0.25 * 0.18 = 0.045
        stats.update(3, 1);

        double expected = 0.0D * 0.82D + 0.25D * 0.18D;
        assertEquals(expected, stats.rollbackRate(), 1e-15,
                "rollbackRate must follow the EWMA formula: prev * 0.82 + batch * 0.18");
    }

    @Test
    void rollbackRateShouldIncreaseWhenRejectedSampleArrives() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(0, 2);
        assertTrue(stats.rollbackRate() > 0.0D,
                "rollbackRate must be positive after a rejected jump sample");
    }

    @Test
    void shouldDecayRollbackRateWhenNoNewSamplesArrive() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(0, 2);
        double beforeDecay = stats.rollbackRate();

        stats.update(0, 0);

        assertTrue(stats.rollbackRate() < beforeDecay,
                "rollbackRate must decay when an empty sample is submitted");
    }

    @Test
    void shouldDecayRollbackRateToZeroWhenBelowEpsilon() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(0, 1);
        for (int i = 0; i < 10000; i++) {
            stats.update(0, 0);
        }
        assertEquals(0.0D, stats.rollbackRate(),
                "rollbackRate must collapse to exactly zero once it falls below the epsilon threshold");
    }

    @Test
    void shouldClampNegativeArgumentsToZeroAndNotDecrementCounters() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(5, 2);

        int confirmedBefore = stats.confirmedJumps();
        int rejectedBefore = stats.rejectedJumps();

        stats.update(-10, -20);

        assertEquals(confirmedBefore, stats.confirmedJumps(),
                "Negative confirmedDelta must be clamped to zero");
        assertEquals(rejectedBefore, stats.rejectedJumps(),
                "Negative rejectedDelta must be clamped to zero");
    }

    @Test
    void rollbackRateMustNeverExceedOne() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        for (int i = 0; i < 1000; i++) {
            stats.update(0, 100);
        }
        assertTrue(stats.rollbackRate() <= 1.0D,
                "rollbackRate must never exceed 1.0 regardless of how many rejections are recorded");
    }

    @Test
    void shouldAccumulateAcrossMultipleUpdates() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(2, 0);
        stats.update(3, 1);
        stats.update(0, 1);

        assertEquals(5, stats.confirmedJumps());
        assertEquals(2, stats.rejectedJumps());
    }
}
