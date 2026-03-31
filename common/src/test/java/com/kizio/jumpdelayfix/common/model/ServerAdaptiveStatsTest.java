package com.kizio.jumpdelayfix.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerAdaptiveStatsTest {

    @Test
    void shouldAccumulateCountersAndSmoothRollbackRate() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();

        stats.update(3, 1);

        assertEquals(3, stats.confirmedJumps());
        assertEquals(1, stats.rejectedJumps());
        assertTrue(stats.rollbackRate() > 0.0D);
    }

    @Test
    void shouldDecayRollbackRateWhenNoNewSamplesArrive() {
        ServerAdaptiveStats stats = new ServerAdaptiveStats();
        stats.update(0, 2);
        double beforeDecay = stats.rollbackRate();

        stats.update(0, 0);

        assertTrue(stats.rollbackRate() < beforeDecay);
    }
}
