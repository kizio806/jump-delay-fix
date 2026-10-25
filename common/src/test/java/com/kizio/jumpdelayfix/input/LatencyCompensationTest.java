package com.kizio.jumpdelayfix.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LatencyCompensationTest {

    @Test
    void shouldReturnFallbackOnUnknownOrNegativeLatency() {
        assertEquals(
                LatencyCompensation.UNKNOWN_LATENCY_FALLBACK_TICKS,
                LatencyCompensation.mapLatencyToRequiredTicks(LatencyCompensation.UNKNOWN_LATENCY_MS)
        );
        assertEquals(
                LatencyCompensation.UNKNOWN_LATENCY_FALLBACK_TICKS,
                LatencyCompensation.mapLatencyToRequiredTicks(-1)
        );
        assertEquals(
                LatencyCompensation.UNKNOWN_LATENCY_FALLBACK_TICKS,
                LatencyCompensation.mapLatencyToRequiredTicks(-2)
        );
        assertEquals(
                LatencyCompensation.UNKNOWN_LATENCY_FALLBACK_TICKS,
                LatencyCompensation.mapLatencyToRequiredTicks(-50)
        );
        assertEquals(
                LatencyCompensation.UNKNOWN_LATENCY_FALLBACK_TICKS,
                LatencyCompensation.mapLatencyToRequiredTicks(Integer.MIN_VALUE)
        );
    }

    @Test
    void shouldMapLowLatency() {
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_LOW_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(0)
        );
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_LOW_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(80)
        );
    }

    @Test
    void shouldMapMediumLatency() {
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_MEDIUM_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(81)
        );
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_MEDIUM_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(150)
        );
    }

    @Test
    void shouldMapHighLatency() {
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_HIGH_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(151)
        );
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_HIGH_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(250)
        );
    }

    @Test
    void shouldMapVeryHighLatency() {
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_VERY_HIGH_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(251)
        );
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_VERY_HIGH_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(600)
        );
        assertEquals(
                LatencyCompensation.REMOTE_TICKS_VERY_HIGH_LATENCY,
                LatencyCompensation.mapLatencyToRequiredTicks(Integer.MAX_VALUE)
        );
    }
}
